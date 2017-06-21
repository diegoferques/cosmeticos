# cosmeticos
Projeto sem nome definido, estamos aguardando o cliente.

# Configuração do Ambiente
- Instalar a versão mais recente do JDK8 para o seu sistema operacional: http://www.oracle.com/technetwork/pt/java/javase/downloads/jdk8-downloads-2133151.html

- Certifique-se de que a variável de ambiente JAVA_HOME contem o caminho para onde foi instalado o Java e que a variável de ambiente PATH possua, dentre outros valores, o valor %JAVA_HOME%\bin (windows) ou $JAVA_HOME/bin (linux).

- Instalar o GIT: https://git-scm.com/downloads

- Baixar e descompactar o Maven (Não necessita desinstalação). Incluir o caminho onde será armazenado o maven na variável de ambiente MAVEN_HOME.

- Instalar InteliJ ou qualquer IDE de sua preferencia.

- Instalar o plugin do Lombok disponível para sua IDE.

- Defina um diretorio onde será baixado o projeto e execute: git clone https://github.com/diegoferques/cosmeticos.git
-- Obs: com a contratação do Bitbucket o endereço de clone será alterado.

- Caso esteja usando a IDE, localize a classe Application.java e execute-a. Normalmente as IDEs permitem o clique com direito na classe, Executar.

- Caso queira executar independentemente da IDE, no diretorio raiz do projeto (onde encontra-se o pom.xml) execute: mvn clean install spring-boot:run -DskipTests

- Pronto, só aguardar o Tomcat finalizar o startup
