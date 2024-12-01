package Model;

import Bean.PagamentosBean;
import Main.IdGenerator;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.result.DeleteResult;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import org.bson.Document;
import org.bson.conversions.Bson;

public class PagamentosModel {

    public static boolean createPagamento(PagamentosBean pagamento, MongoDatabase database) {
        try {
            MongoCollection<Document> pagamentosCollection = database.getCollection("pagamentos");
            IdGenerator idGen = new IdGenerator(database);
            Integer idPagamento = idGen.getNextId("pagamentoId");

            Document pagamentoDoc = new Document("id_pagamento", idPagamento)
                    .append("valor_pago", pagamento.getValorPago())
                    .append("status", pagamento.getStatus())
                    .append("id_emprestimo", pagamento.getIdEmprestimo());

            pagamentosCollection.insertOne(pagamentoDoc);
            pagamento.setIdPagamento(idPagamento);
            return true;
        } catch (Exception e) {
            System.err.println("Erro ao criar pagamento: " + e.getMessage());
            return false;
        }
    }

    public static LinkedHashSet<PagamentosBean> listarPagamentos(Integer idEmprestimo, MongoDatabase database) {
        LinkedHashSet<PagamentosBean> pagamentos = new LinkedHashSet<>();
        MongoCollection<Document> emprestimosCollection = database.getCollection("emprestimos");
        MongoCollection<Document> pagamentosCollection = database.getCollection("pagamentos");

        try {
            Document emprestimo = emprestimosCollection.find(new Document("id_emprestimo", idEmprestimo)).first();

            if (emprestimo != null) {
                double valorEmprestimo = emprestimo.getDouble("valor_emprestimo");

                double totalPago = pagamentosCollection.aggregate(Arrays.asList(
                    Aggregates.match(Filters.eq("id_emprestimo", idEmprestimo)),
                    Aggregates.group("$id_emprestimo", Accumulators.sum("total_pago", "$valor_pago"))
                )).into(new ArrayList<>()).stream()
                  .map(doc -> doc.getDouble("total_pago"))
                  .findFirst().orElse(0.0);

                Document mostRecentPayment = pagamentosCollection.find(Filters.eq("id_emprestimo", idEmprestimo))
                    .sort(Sorts.descending("id_pagamento"))
                    .first();

                int statusFinal = mostRecentPayment != null ? mostRecentPayment.getInteger("status") : 1;

                String statusDescricao;
                switch (statusFinal) {
                    case 1 -> statusDescricao = "Pendente";
                    case 2 -> statusDescricao = "Pago";
                    case 3 -> statusDescricao = "Atrasado";
                    default -> statusDescricao = "Pendente";
                }

                PagamentosBean pagamento = new PagamentosBean(idEmprestimo, valorEmprestimo, totalPago, statusDescricao);
                pagamentos.add(pagamento);
            }
        } catch (Exception e) {
            System.err.println("Erro ao listar pagamentos por empréstimo: " + e.getMessage());
        }

        return pagamentos;
    }


    public static boolean atualizarPagamento(Integer idEmprestimo, double valorPago, MongoDatabase database) {
        try {
            MongoCollection<Document> pagamentosCollection = database.getCollection("pagamentos");

            Document pagamento = pagamentosCollection.find(Filters.eq("id_emprestimo", idEmprestimo)).first();

            if (pagamento != null) {
                double novoValorPago = pagamento.getDouble("valor_pago") + valorPago;

                Document updatedPayment = new Document("$set", new Document("valor_pago", novoValorPago)
                    .append("status", 2));

                pagamentosCollection.updateOne(Filters.eq("id_emprestimo", idEmprestimo), updatedPayment);
                return true;
            } else {
                System.err.println("Pagamento não encontrado para o empréstimo: " + idEmprestimo);
            }
        } catch (Exception e) {
            System.err.println("Erro ao atualizar o pagamento: " + e.getMessage());
        }
        return false;
    }

    
    public double getValorPagoTotal(Integer idEmprestimo, MongoDatabase database) {
        try {
            MongoCollection<Document> pagamentosCollection = database.getCollection("pagamentos");

            Bson filter = Filters.eq("id_emprestimo", idEmprestimo);

            AggregateIterable<Document> result = pagamentosCollection.aggregate(Arrays.asList(
                Aggregates.match(filter),
                Aggregates.group(null, Accumulators.sum("valor_pago_total", "$valor_pago"))
            ));

            Document document = result.first();
            if (document != null) {
                return document.getDouble("valor_pago_total");
            }
        } catch (Exception e) {
            System.err.println("Erro ao obter o valor total pago: " + e.getMessage());
        }
        return 0;
    }
    
    public static PagamentosBean getPagamentoPorEmprestimo(Integer idEmprestimo, MongoDatabase database) {
        try {
            MongoCollection<Document> pagamentosCollection = database.getCollection("pagamentos");

            Bson filter = Filters.eq("id_emprestimo", idEmprestimo);

            Document pagamentoDocument = pagamentosCollection.find(filter).first();

            if (pagamentoDocument != null) {
                Integer idPagamento = pagamentoDocument.getInteger("id_pagamento");
                double valorPago = pagamentoDocument.getDouble("valor_pago");
                int status = pagamentoDocument.getInteger("status");

                return new PagamentosBean(idPagamento, valorPago, status, idEmprestimo);
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar pagamento por empréstimo: " + e.getMessage());
        }
        return null;
    }
    
    public static boolean existePagamento(Integer idPagamento, MongoDatabase database) {
        try {
            MongoCollection<Document> pagamentosCollection = database.getCollection("pagamentos");
            Bson filter = Filters.eq("id_pagamento", idPagamento);
            long count = pagamentosCollection.countDocuments(filter);

            return count > 0;
        } catch (Exception e) {
            System.err.println("Erro ao verificar existência de pagamento: " + e.getMessage());
            return false;
        }
    }

    public static boolean excluirPagamento(Integer idPagamento, MongoDatabase database) {
        try {
            MongoCollection<Document> pagamentosCollection = database.getCollection("pagamentos");
            Bson filter = Filters.eq("id_pagamento", idPagamento);
            DeleteResult result = pagamentosCollection.deleteOne(filter);
            return result.getDeletedCount() > 0;
        } catch (Exception e) {
            System.err.println("Erro ao excluir pagamento: " + e.getMessage());
            return false;
        }
    }

    
    public static LinkedHashSet<PagamentosBean> listarPagamentos(MongoDatabase database) {
        LinkedHashSet<PagamentosBean> pagamentos = new LinkedHashSet<>();
        try {
            MongoCollection<Document> pagamentosCollection = database.getCollection("pagamentos");
            FindIterable<Document> resultado = pagamentosCollection.find();

            for (Document doc : resultado) {
                Integer idPagamento = doc.getInteger("id_pagamento");
                double valorPago = doc.getDouble("valor_pago");
                PagamentosBean pagamento = new PagamentosBean(idPagamento, valorPago);
                pagamentos.add(pagamento);
            }
        } catch (Exception e) {
            System.err.println("Erro ao listar pagamentos: " + e.getMessage());
        }
        return pagamentos;
    }
}
