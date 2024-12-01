Para a execução do sistema, instalar o MongoDb Compass na versão 8.0.3 disponivel em https://www.mongodb.com/try/download/community.

Executar o arquivo baixado para a instalação. Ao final da instalação, criar uma conexão, garantir que a URI seja “mongodb://localhost:27017”. Criar um banco “LibraSys” e criar as collections “sessoes”, “clientes”, “livros”, “emprestimos”, ‘pagamentos”, “concessao”, “counters”.
Importar os arquivos de backup de cada collection, importar por ultimo a collection “counters” para não ter problema com os indices.

Pode ser necessário adicionar a dependencia, verificar nas pasta Project Files no arquivo pom.xml se está adicionado, caso não esteja, seguir o passo abaixo.
Adicionar a dependência do Mongo na versão 3.12.14. Copiar o codigo abaixo e colar no arquivo pom.xml dentro da tag project. 
O codigo tambem esta disponivel em https://mvnrepository.com/artifact/org.mongodb/mongo-java-driver

<!-- https://mvnrepository.com/artifact/org.mongodb/mongo-java-driver -->
<dependency>
    <groupId>org.mongodb</groupId>
    <artifactId>mongo-java-driver</artifactId>
    <version>3.12.14</version>
</dependency>
