package Model;

import Bean.EmprestimosBean;
import Main.IdGenerator;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.bson.Document;

public class EmprestimosModel {

public static int createEmprestimo(EmprestimosBean emprestimo, Integer[] livros, MongoDatabase database) {
        MongoCollection<Document> emprestimosCollection = database.getCollection("emprestimos");
        MongoCollection<Document> livrosCollection = database.getCollection("livros");

        IdGenerator idGen = new IdGenerator(database);
        Integer IdEmprestimo = idGen.getNextId("emprestimoId");

        Document emprestimoDoc = new Document("id_emprestimo", IdEmprestimo)
                .append("id_cliente", emprestimo.getIdCliente())
                .append("data_emprestimo", emprestimo.getDataEmprestimo())
                .append("data_devolucao", emprestimo.getDataDevolucao())
                .append("valor_emprestimo", emprestimo.getvalorEmprestimo())
                .append("status", emprestimo.getStatusEmp());

        try {
            emprestimosCollection.insertOne(emprestimoDoc);

            Integer idEmprestimo = emprestimoDoc.getInteger("id_emprestimo");

            for (Integer idLivro : livros) {
                Document livroDoc = livrosCollection.find(Filters.eq("id_livro", idLivro)).first();

                if (livroDoc != null) {
                    int quantidadeDisponivel = livroDoc.getInteger("quantidade_disponivel");

                    if (quantidadeDisponivel > 0) {
                        livrosCollection.updateOne(
                            Filters.eq("id_livro", idLivro),
                            new Document("$inc", new Document("quantidade_disponivel", -1))
                        );

                        boolean concessaoCriada = createConcessao(idEmprestimo, idLivro, database);

                        if (!concessaoCriada) {
                            livrosCollection.updateOne(
                                Filters.eq("id_livro", idLivro),
                                new Document("$inc", new Document("quantidade_disponivel", 1))
                            );
                            return 0;
                        }
                    } else {
                        return 0;
                    }
                }
            }
            return idEmprestimo;
        } catch (Exception e) {
            System.err.println("Erro ao criar o empréstimo: " + e.getMessage());
            return 0;
        }
    }
    
    public static boolean createConcessao(Integer idEmprestimo, Integer idLivro, MongoDatabase database) {
        MongoCollection<Document> concessaoCollection = database.getCollection("concessao");

        Document concessaoDoc = new Document("id_emprestimo", idEmprestimo)
                .append("id_livro", idLivro);

        try {
            concessaoCollection.insertOne(concessaoDoc);
            return true;
        } catch (Exception e) {
            System.err.println("Erro ao associar livro ao empréstimo: " + e.getMessage());
            return false;
        }
    }

    public static boolean alterarEmprestimo(Integer idEmprestimo, Date novaDataDevolucao, MongoDatabase database) {
        MongoCollection<Document> emprestimosCollection = database.getCollection("emprestimos");

        Document filter = new Document("id_emprestimo", idEmprestimo);

        Document update = new Document("$set", new Document("data_devolucao", novaDataDevolucao)
                .append("status", 1));

        try {
            long matchedCount = emprestimosCollection.updateOne(filter, update).getModifiedCount();
            return matchedCount > 0;
        } catch (Exception e) {
            System.err.println("Erro ao alterar o empréstimo: " + e.getMessage());
            return false;
        }
    }

    public static boolean excluirEmprestimo(Integer idEmprestimo, MongoDatabase database) {
        MongoCollection<Document> emprestimosCollection = database.getCollection("emprestimos");
        MongoCollection<Document> livrosCollection = database.getCollection("livros");
        MongoCollection<Document> concessaoCollection = database.getCollection("concessao");

        try {
            FindIterable<Document> concessaoDocs = concessaoCollection.find(Filters.eq("id_emprestimo", idEmprestimo));
            for (Document doc : concessaoDocs) {
                Integer idLivro = doc.getInteger("id_livro");
                livrosCollection.updateOne(
                    Filters.eq("id_livro", idLivro),
                    Updates.inc("quantidade_disponivel", 1)
                );
            }

            concessaoCollection.deleteMany(Filters.eq("id_emprestimo", idEmprestimo));

            long deletedCount = emprestimosCollection.deleteOne(Filters.eq("id_emprestimo", idEmprestimo)).getDeletedCount();

            return deletedCount > 0;
        } catch (Exception e) {
            System.err.println("Erro ao excluir o empréstimo: " + e.getMessage());
            return false;
        }
    }

    public static boolean temPagamentosRelacionados(Integer idEmprestimo, MongoDatabase database) {
        MongoCollection<Document> pagamentosCollection = database.getCollection("pagamentos");

        long count = pagamentosCollection.countDocuments(Filters.eq("id_emprestimo", idEmprestimo));

        return count > 0;
    }

    public static LinkedHashSet<EmprestimosBean> listarEmprestimos(MongoDatabase database) {
        LinkedHashMap<Integer, EmprestimosBean> emprestimosMap = new LinkedHashMap<>();

        MongoCollection<Document> emprestimosCollection = database.getCollection("emprestimos");
        MongoCollection<Document> clientesCollection = database.getCollection("clientes");
        MongoCollection<Document> concessaoCollection = database.getCollection("concessao");
        MongoCollection<Document> livrosCollection = database.getCollection("livros");
        MongoCollection<Document> pagamentosCollection = database.getCollection("pagamentos");

        FindIterable<Document> emprestimosDocs = emprestimosCollection.find();

        for (Document emprestimoDoc : emprestimosDocs) {
            Integer idEmprestimo = emprestimoDoc.getInteger("id_emprestimo");

            Document clienteDoc = clientesCollection.find(new Document("id_cliente", emprestimoDoc.getInteger("id_cliente"))).first();
            String nomeCliente = clienteDoc != null ? clienteDoc.getString("nome") : "Desconhecido";

            FindIterable<Document> concessaoDocs = concessaoCollection.find(new Document("id_emprestimo", idEmprestimo));

            Set<String> titulosLivros = new HashSet<>();
            for (Document concessaoDoc : concessaoDocs) {
                Integer idLivro = concessaoDoc.getInteger("id_livro");
                Document livroDoc = livrosCollection.find(new Document("id_livro", idLivro)).first();
                if (livroDoc != null) {
                    titulosLivros.add(livroDoc.getString("titulo"));
                }
            }

            Document pagamentoDoc = pagamentosCollection.find(new Document("id_emprestimo", idEmprestimo)).first();
            String statusPagamentoDescricao = "Pendente";
            if (pagamentoDoc != null) {
                int statusPagamento = pagamentoDoc.getInteger("status", 1);
                switch (statusPagamento) {
                    case 1 -> statusPagamentoDescricao = "Pendente";
                    case 2 -> statusPagamentoDescricao = "Pago";
                    case 3 -> statusPagamentoDescricao = "Atrasado";
                }
            }

            java.sql.Date dataEmprestimo = new java.sql.Date(emprestimoDoc.getDate("data_emprestimo").getTime());
            java.sql.Date dataDevolucao = new java.sql.Date(emprestimoDoc.getDate("data_devolucao").getTime());
            String statusEmprestimo = "";
            if (emprestimoDoc.containsKey("status")) {
                Object statusObj = emprestimoDoc.get("status");
                if (statusObj instanceof Integer) {
                    int status = (Integer) statusObj;
                    switch (status) {
                        case 1 -> statusEmprestimo = "No prazo";
                        case 2 -> statusEmprestimo = "Devolvido";
                        case 3 -> statusEmprestimo = "Atrasado";
                        default -> statusEmprestimo = "Desconhecido";
                    }
                } else if (statusObj instanceof String) {
                    statusEmprestimo = (String) statusObj;
                }
            }
            double valorEmprestimo = emprestimoDoc.getDouble("valor_emprestimo");

            if (!emprestimosMap.containsKey(idEmprestimo)) {
                String primeiroTitulo = titulosLivros.isEmpty() ? "" : titulosLivros.iterator().next();
                EmprestimosBean emprestimo = new EmprestimosBean(idEmprestimo, dataEmprestimo, dataDevolucao, 
                                                                  statusEmprestimo, valorEmprestimo, statusPagamentoDescricao, 
                                                                  primeiroTitulo, nomeCliente);
                for (String titulo : titulosLivros) {
                    if (!titulo.equals(primeiroTitulo)) {
                        emprestimo.addTituloLivro(titulo);
                    }
                }
                emprestimosMap.put(idEmprestimo, emprestimo);
            } else {
                EmprestimosBean emprestimo = emprestimosMap.get(idEmprestimo);
                for (String titulo : titulosLivros) {
                    emprestimo.addTituloLivro(titulo);
                }
            }
        }

        return new LinkedHashSet<>(emprestimosMap.values());
    }
    
    public static LinkedHashSet<EmprestimosBean> listarEmprestimosPendentes(MongoDatabase database) {
        LinkedHashMap<Integer, EmprestimosBean> emprestimosMap = new LinkedHashMap<>();

        MongoCollection<Document> emprestimosCollection = database.getCollection("emprestimos");

        List<org.bson.conversions.Bson> pipeline = List.of(
            Aggregates.lookup("clientes", "id_cliente", "id_cliente", "cliente_info"),
            Aggregates.lookup("concessao", "id_emprestimo", "id_emprestimo", "concessao_info"),
            Aggregates.lookup("livros", "concessao_info.id_livro", "id_livro", "livro_info"),
            Aggregates.lookup("pagamentos", "id_emprestimo", "id_emprestimo", "pagamento_info"),
            Aggregates.match(Filters.ne("status", 2)),
            Aggregates.project(Projections.fields(
                Projections.include("id_emprestimo", "data_emprestimo", "data_devolucao", "valor_emprestimo", "status"),
                Projections.computed("status_emprestimo", new Document("$cond", List.of(
                            new Document("$eq", List.of("$status", 1)), "No prazo",
                            new Document("$cond", List.of(
                                new Document("$eq", List.of("$status", 2)), "Devolvido",
                                "Atrasado"
                            ))
                        ))),
                Projections.computed("nome_cliente", new Document("$arrayElemAt", List.of("$cliente_info.nome", 0))),
                Projections.computed("status_pagamento", new Document("$cond", List.of(
                            new Document("$eq", List.of("$pagamento_info.status", 1)), "Pendente",
                            new Document("$cond", List.of(
                                new Document("$eq", List.of("$pagamento_info.status", 2)), "Pago",
                                "Atrasado"
                            ))
                        ))),
                Projections.computed("titulo_livro", new Document("$arrayElemAt", List.of("$livro_info.titulo", 0)))
            )),
            Aggregates.sort(Sorts.ascending("id_emprestimo"))
        );

        AggregateIterable<Document> results = emprestimosCollection.aggregate(pipeline);

        for (Document doc : results) {
            Integer idEmprestimo = doc.getInteger("id_emprestimo");
            String nomeCliente = doc.getString("nome_cliente");
            java.util.Date dataEmprestimo = doc.getDate("data_emprestimo");
            java.util.Date dataDevolucao = doc.getDate("data_devolucao");
            String statusEmprestimo = doc.getString("status_emprestimo");
            double valorEmprestimo = doc.getDouble("valor_emprestimo");
            String statusPagamento = doc.getString("status_pagamento");
            String tituloLivro = doc.getString("titulo_livro");

            if (!emprestimosMap.containsKey(idEmprestimo)) {
                EmprestimosBean emprestimo = new EmprestimosBean(idEmprestimo, dataEmprestimo, dataDevolucao,
                        statusEmprestimo, valorEmprestimo, statusPagamento, tituloLivro, nomeCliente);
                emprestimosMap.put(idEmprestimo, emprestimo);
            }

            EmprestimosBean emprestimo = emprestimosMap.get(idEmprestimo);
            emprestimo.addTituloLivro(tituloLivro);
        }

        LinkedHashSet<EmprestimosBean> emprestimos = new LinkedHashSet<>(emprestimosMap.values());
        return emprestimos;
    }
    
    public static LinkedHashSet<EmprestimosBean> listarEmprestimosSemPagamento(MongoDatabase database) {
        LinkedHashMap<Integer, EmprestimosBean> emprestimosMap = new LinkedHashMap<>();

        MongoCollection<Document> emprestimosCollection = database.getCollection("emprestimos");

        List<org.bson.conversions.Bson> pipeline = List.of(
            Aggregates.lookup("clientes",   "id_cliente", "id_cliente", "cliente_info"),
            Aggregates.lookup("concessao", "id_emprestimo", "id_emprestimo", "concessao_info"),
            Aggregates.lookup("livros", "concessao_info.id_livro", "id_livro", "livro_info"),
            Aggregates.lookup("pagamentos", "id_emprestimo", "id_emprestimo", "pagamento_info"),
            Aggregates.match(Filters.expr(new Document("$or", List.of(
                new Document("$lt", List.of(new Document("$arrayElemAt", List.of("$pagamento_info.valor_pago", 0)), "$valor_emprestimo")),
                new Document("$eq", List.of(new Document("$arrayElemAt", List.of("$pagamento_info.id_pagamento", 0)), 0))
            )))),
            Aggregates.project(Projections.fields(
                Projections.include("id_emprestimo", "data_emprestimo", "data_devolucao", "valor_emprestimo", "status"),
                Projections.computed("status_emprestimo", new Document("$switch", new Document("branches", List.of(
                                new Document("case", new Document("$eq", List.of("$status", 1)))
                                    .append("then", "No prazo"),
                                new Document("case", new Document("$eq", List.of("$status", 2)))
                                    .append("then", "Devolvido")
                            ))
                            .append("default", "Atrasado")
                        )),
                Projections.computed("nome_cliente", new Document("$arrayElemAt", List.of("$cliente_info.nome", 0))),
                Projections.computed("titulo_livro", new Document("$arrayElemAt", List.of("$livro_info.titulo", 0)))

            )),
            Aggregates.sort(Sorts.ascending("id_emprestimo"))
        );

        AggregateIterable<Document> results = emprestimosCollection.aggregate(pipeline);

        for (Document doc : results) {
            Integer idEmprestimo = doc.getInteger("id_emprestimo");
            String nomeCliente = doc.getString("nome_cliente");
            java.util.Date dataEmprestimo = doc.getDate("data_emprestimo");
            java.util.Date dataDevolucao = doc.getDate("data_devolucao");
            String statusEmprestimo = doc.getString("status_emprestimo");
            double valorEmprestimo = doc.getDouble("valor_emprestimo");
            String tituloLivro = doc.getString("titulo_livro");

            if (!emprestimosMap.containsKey(idEmprestimo)) {
                EmprestimosBean emprestimo = new EmprestimosBean(idEmprestimo, dataEmprestimo, dataDevolucao,
                        statusEmprestimo, valorEmprestimo, "Pendente", tituloLivro, nomeCliente);
                emprestimosMap.put(idEmprestimo, emprestimo);
            }

            EmprestimosBean emprestimo = emprestimosMap.get(idEmprestimo);
            emprestimo.addTituloLivro(tituloLivro);
        }

        LinkedHashSet<EmprestimosBean> emprestimos = new LinkedHashSet<>(emprestimosMap.values());
        return emprestimos;
    }

    public double getValorEmprestimo(Integer idEmprestimo, MongoDatabase database) {
        MongoCollection<Document> emprestimosCollection = database.getCollection("emprestimos");

        Document query = new Document("id_emprestimo", idEmprestimo);
        Document result = emprestimosCollection.find(query).first();

        if (result != null) {
            return result.getDouble("valor_emprestimo");
        }

        return 0;
    }
    
    public static void atualizarStatusEmprestimos(MongoDatabase database) {
        MongoCollection<Document> emprestimosCollection = database.getCollection("emprestimos");

        LocalDate currentDate = LocalDate.now();
        Document query = new Document("data_devolucao", new Document("$lt", currentDate))
                            .append("status", 1);

        Document update = new Document("$set", new Document("status", 3));

        UpdateResult result = emprestimosCollection.updateMany(query, update);

        System.out.println(result.getModifiedCount() + " empréstimos atualizados para 'Atrasado'.");
    }
    
    public static boolean verificarEmprestimoPago(Integer idEmprestimo, MongoDatabase database) {
        MongoCollection<Document> emprestimosCollection = database.getCollection("emprestimos");
        MongoCollection<Document> pagamentosCollection = database.getCollection("pagamentos");

        Document query = new Document("id_emprestimo", idEmprestimo);
        Document emprestimo = emprestimosCollection.find(query).first();

        if (emprestimo != null) {
            double valorEmprestimo = emprestimo.getDouble("valor_emprestimo");

            Document pagamentoQuery = new Document("id_emprestimo", idEmprestimo);
            AggregateIterable<Document> pagamentoResult = pagamentosCollection.aggregate(
                Arrays.asList(
                    new Document("$match", pagamentoQuery),
                    new Document("$group", new Document("_id", null)
                        .append("valor_pago", new Document("$sum", "$valor_pago")))
                )
            );

            double valorPago = 0;
            if (pagamentoResult.iterator().hasNext()) {
                valorPago = pagamentoResult.iterator().next().getDouble("valor_pago");
            }

            return valorEmprestimo == valorPago;
        }

        return false;
    }
    
    public static boolean darBaixaEmprestimo(Integer idEmprestimo, MongoDatabase database) {
        MongoCollection<Document> emprestimosCollection = database.getCollection("emprestimos");
        MongoCollection<Document> livrosCollection = database.getCollection("livros");
        MongoCollection<Document> concessaoCollection = database.getCollection("concessao");

        Document queryEmprestimo = new Document("id_emprestimo", idEmprestimo);
        Document updateEmprestimo = new Document("$set", new Document("status", 2));

        UpdateResult resultEmprestimo = emprestimosCollection.updateOne(queryEmprestimo, updateEmprestimo);
        if (resultEmprestimo.getMatchedCount() > 0) {
            Document queryConcessao = new Document("id_emprestimo", idEmprestimo);
            FindIterable<Document> concessao = concessaoCollection.find(queryConcessao);

            for (Document concessaoDoc : concessao) {
                Integer idLivro = concessaoDoc.getInteger("id_livro");
                Document queryLivro = new Document("id_livro", idLivro);
                Document updateLivro = new Document("$inc", new Document("quantidade_disponivel", 1));

                UpdateResult resultLivro = livrosCollection.updateOne(queryLivro, updateLivro);
                if (resultLivro.getMatchedCount() == 0) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    public static boolean isEmprestimoDevolvido(Integer idEmprestimo,MongoDatabase database) {
        MongoCollection<Document> emprestimosCollection = database.getCollection("emprestimos");

        Document emprestimo = emprestimosCollection.find(Filters.and(
                Filters.eq("id_emprestimo", idEmprestimo),
                Filters.eq("status", 2)
        )).first();

        return emprestimo != null;
    }
    
    public static boolean emprestimoExiste(MongoDatabase database, Integer idEmprestimo) {
        return database.getCollection("emprestimos")
                .find(new Document("id_emprestimo", idEmprestimo))
                .iterator()
                .hasNext();
    }
}
