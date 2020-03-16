# Geral
Projeto base para criação de novos projetos Springboot via devConsole.
Este projeto entrega uma aplicação java+spring funcional, realizando deploy em desenvolvimento e homologação.

# Branchs
 *https://git.sicredi.net/engenharia-software/wiki-desenvolvimento/tree/master/docs/sicredi_flow*

# .gitlab-ci.yml
Arquivo responsável por orquestar a pipeline do projeto.<br>
As configuracoes de LIVENESS_PROBE, READINESS_PROBE e APP_PORT estao na devconsole<br>
Por padrão, a pipeline vem com as seguintes stages:<br>

1. build: O passo de build agora também é responsável pela criação da imagem Docker, possuindo configurações específicas. Desta forma é importante que somente em casos específicos não seja utilizado a forma padrão de build (maven, gradle{w}...)
2. deploy: Não ha necessida de alteracao, sendo assim foi encurtado e padronizado
   pelo arquivo do sicredi-devconsole-ci.yaml
   deploy é feito em:
    - Desenvolvimento (Branch Master)
    - Homologação, Pré-Produção (Criar uma tag a partir da Master)
    - Produção (Instalação feita por qualquer integrante do time, no entanto o PO deve aprovar previamente a partir da devconsole: Na aba deployment -> Uat (Botao de aprovar)
4. Instalacao de monitoramento como codigo
   *http://monitor.sicredi.net*

# Recursos do POD
Estão na devconsole
  - devconsole 
    - Aplicação 
      - Componente 
        - Deployment 
          - Geral

Funcionamento:
**https://kubernetes.io/docs/concepts/configuration/manage-compute-resources-container/**

# Parametros da heap

Estão na devconsole; 
  - devconsole 
    - Aplicação 
      - Componente 
        - Deployment 
          - (Des/Uat/Stress/Prod) 
            - Environment Variables

# LivenessProbe e readinessProbe
São os endpoints que o Openshift/Kubernetes testam afim de validar a saúde da aplicação.<br>
*https://kubernetes.io/docs/tasks/configure-pod-container/configure-liveness-readiness-probes/*
  - devconsole 
    - Aplicação 
      - Componente 
        - Deployment 
          - Geral

# pom.xml
Para gerenciamento de dependências e build é utilizado o Maven, entregando um POM com todas as dependências mínimas para que a API rode em todas as tecnologias com sucesso. Caso o time opte pela utilização do Gradle,é muito importante que dependências hoje existentes no POM sejam mantidas.

## Google JIB
O Dockerfile não existe mais neste novo template pois passamos a utilizar um plugin da Google (Jib) que é responsável buildar a aplicação e empacotar o container, enviando a imagem gerada para o Harbor. Todas as configurações que antes ficavam no Dockerfile, agora estão contidas no POM, conforme abaixo:
```
<plugin>
                <groupId>com.google.cloud.tools</groupId>
                <artifactId>jib-maven-plugin</artifactId>
                <version>1.4.0</version>
                <configuration>
                    <allowInsecureRegistries>true</allowInsecureRegistries>
                    <from>
                        <image>openjdk.docker.sicredi.net/openjdk:11-minimal</image>
                    </from>
                    <container>
                        <ports>8080</ports>
                        <jvmFlags>
                            <jvmFlag>-noverify</jvmFlag>
                            <jvmFlag>-XX:TieredStopAtLevel=1</jvmFlag>
                        </jvmFlags>
                    </container>
                </configuration>
</plugin>
```
A documentação das configurações que podem ser adicionadas para a criação do container, podem ser encontradas no github do plugin: https://github.com/GoogleContainerTools/jib

# logback-spring (/src/main/resources)
Assim como o POM, o logback que é entregue como padrão no projeto já vem com customizações para que o envio de logs ocorra com sucesso.<br>
Este template já vem configurado para utilizar dois appenders:
1. GELF (Envio para o kafka o qual eh consumido pelo Graylog/ElasticSearch)
 Para encontrar o log, procure pela stream do<br>
 **[nome do grupo]|PODS**<br>
2. STDOUT (Envio do log para o pod) Nao recomendado, pois a escrita no pod onera a performance. Utilizar em casos específicos.

# bootstrap.yml
Arquivo de propriedades utilizado pelo Spring. Aqui ficam todas as propriedades da aplicação que não são alteradas com frequência. Para propriedades que podem sofrer maiores alterações, que não precisam gerar um novo deploy para funcionamento, deve-se usar o Consul.
Para um novo projeto, precisam ser alteradas as informações da tag application:
```
application:
  group: demoinfra
  name: vault-springboot-starter-demo
```

# Consul
Qualquer propriedade da aplicação deve ser configurada no consul, através da "devconsole -> Aplicação -> Componente -> Consul/KV"
Sistema responsável por guardar as propriedades da aplicação (para programadores acostumados a desenvolver ferramentas para weblogic, essse é o application.properties).
O propósito de usar o Consul é poder manipular propriedades "a quente", ou seja, a aplicação não precisará ser reiniciada para substituir as propriedades substituídas no consul.
Na nossa estrutura atual, existe um para cada ambiente. A aplicação consegue identificar qual o ambiente deve usar através de um config_map.
Abaixo segue um simples exemplo de uso:
```
datasource:
  meuds:
    url: jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS_LIST=(ADDRESS=(PROTOCOL=TCP)(HOST=DB)(PORT=PORT)))(CONNECT_DATA=(service_name=SERVICE_NAME)(SERVER=DEDICATED)))
    username: USERNAME_RUN
    password: ${oracle.coredb.username_run.password}
    driverClassName: oracle.jdbc.OracleDriver
```
É interessante verificar que a variável password está sendo buscada do Vault.
As configurações devem ser feitas por projetos na devConsole (devconsole.sicredi.net)

Funcionamento:
 *https://spring.io/projects/spring-cloud-consul*
 *https://www.consul.io/*

# Vault
**Qualquer segredo da aplicação deve ser salva no vault.**<br>
Sistema é utilizada para armazenar secrets (senhas) inseridas nos arquivos de propriedades (Consul / bootstrap.yml).<br>
Assim como Consul, também existe Vault um por ambiente, sendo identificado qual deve ser utilizado através de um config_map.<br>
Todas as senhas de usuários que irão conectar em banco de dados devem estar cadastradas no Vault. Senhas/tokens utilizados pela aplicação também podem ser armazenados no vault.<br>
A configuração que permite que uma aplicação tenha acesso a uma secret deve ser realizada via devConsole.<br>
OBS: Os times não possuem acesso direto ao Vault, apenas ao Consul. Sendo assim, novas secrets devem ser solicitadas aos engenheiro devops.<br>
Utilização no Sicredi:<br>
Para utilizar os segredos temos duas partições:
1. Para segredos de banco de dados:
```
lookup:
  - root-path: db
      keys:
      - oracle.coredb.middleware_run.password
```
Padrão: [**tipo_de_banco.nome_do_banco.usuario**.password]

2. Para segredos de aplicação: Por exemplo: token oauth"
lookup:
```
  - root-path: app/${spring.application.name}
      keys:
      - token_oauth_minha_api
```
Padrão: **[qualquer_nome_de_propriedade]**
---
Alguns links externos para referência de leitura:
 *https://www.vaultproject.io/*
 *https://cloud.spring.io/spring-cloud-vault/*

---
# Monitoramento
Dentro do diretório monitoramento/ existe um arquivo no formato JSON que será importado para dentro do Grafana.<br>
No etapa "**Instalacao Monitoramento**", que é manual, é importado o arquivo para dentro da ferramenta.<br>
```
Acesso a ferramenta: http://monitor.sicredi.net/grafana
```
Neste monitoramento, temos alertas configurados para enviar ao time no rocket (nome_do_time-devops) durante o horario comercial<br>
e fora deste horário, será acionado o sobreaviso, em caso de anomalias, que por padrão estão descritar abaixo:<br>
O template de monitoramento tem:<br>
<br>
    **Replicas disponíveis**: Se atingir 50% das replicas o time será acionado.<br>
    **Error code 500**: Qualquer erro 500<br>
    **Tempo de resposta**: acima de 30s<br>
    **Requests**: Sem alerta definido.<br>
<br>
Este monitoramento pode ser customizado pelo time, incluindo ou excluindo qualquer um dos itens.<br>
Se for feito esta customização, exporte o grafana para um arquivo JSON e coloque como código no diretório novamente. Pois no próximo processo de instalação será importado o arquivo. Isto se dá pelo fato de utilizarmos o monitoramento como código. Para exportar:<br>
<br>
    Clique em **Share dashboard** -> **Export** -> **View JSON**

---
# Graylog
Ferramenta utilizada para visualização de logs. Possui rápido acesso via devconsole.
Link de acesso:
 *http://graylog.sicredi.net*
Alguns links externos para referência de leitura
 *https://docs.graylog.org/en/2.5/*

---
# Artifactory
Ferramenta utilizada para gerência/armazenamento de artefatos.
 *https://www.jfrog.com/confluence/display/RTF/General+Information*


