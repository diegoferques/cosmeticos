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


# Fluxo de changes
- Os fluxos seguirão, em sua essencia, o conhecido fluxo Git Flow: ótimo post sobre as práticas do git flow https://www.atlassian.com/git/tutorials/comparing-workflows.

- As branches MASTER e DEV jamais podem receber commites. 

- Todo trabalho deve ser iniciado a partir da branch DEV mas nenhuma linha de codigo pode ser escrita nela. Para começar o desenvolvimento, mova-se para a branch dev com: git checkout dev. Depois crie uma nova branch com todo o conteúdo de dev: git checkout -b MINHA_NOVA_BRANCH_PESSOAL.

- As alterações realizadas em sua branch só poderá ser agregada à branch dev mediante PULL REQUEST. Para tal, faça o push de suas alterações para o servidor remoto. Acesse a branch pelo browser do servidor git e clique em NEW PULL REQUEST, selecione os avaliadores, aguarde que eles aprovem suas alterações e depois clique em MERGE.

- Mantenha a branch DEV sempre atualizada no repositorio local. Faça pull nela constantemente.

- A branch MASTER só pode ser atualizada quando a change estiver indo para produção e o merge tem que ser feito exclusivamente com a branch DEV.

# Endpoints REST
- professionalcategory/search: utiliza o termo de busca (query) para filtrar 3 dados: nome da categoria, nome da regra de preco e nome do profissional. Vide ProfessionalCategoryRepository. Como a propria query determina, apenas categorias que possuam tabela de preço e associadas a profissionais serao retornadas. 


# Migração de um repositorio para outro
git init
git remote add origin https://github.com/garrydias/ipretty.git 
git status
git pull
git pull origin master
git checkout dev
git remote add garry https://github.com/garrydias/ipretty.git
git push garry '*:*'
git checkout master
git pull
git push -f garry '*:*'
git fetch origin
git branch -l
for remote in `git branch -r`; do git branch --track ${remote#origin/} $remote; done
git branch -l
git fetch --all
git pull --all
git branch -l
git push -f garry '*:*'
