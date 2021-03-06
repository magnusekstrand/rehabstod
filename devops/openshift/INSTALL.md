
# OPENSHIFT INSTALLATION GUIDE

Installation of Web application rehabstod on openshift.

## 1 Pre-Installation Requirements

The following prerequisites and requirements must be satisfied in order for the rehabstod to install successfully.


### 1.1 Backing Service Dependencies

The application has the following external services: 

Provided by operations (execution environment):

* MySQL (provided)
* ActiveMQ (provided)
* Redis Sentinel (provided)
* Redis Server (provided)

Provided elsewhere:

* Inera Service Platform (NTjP)

For all backing services their actual addresses and user accounts have to be known prior to start the installation procedure.  

### 1.2 Integration / Firewall

Rehabstöd communicates in/out with the Inera Service Platform and thus needs firewall rules for that access.

### 1.3 Certificates

Rehabstöd needs certificates, keystores and truststores for communicating over Tjänsteplattformen. The operations provider is responsible for installing these certificates in the appropriate OpenShift "secret", see detailed instructions in the OpenShift section.

### 1.4 Message Queues

Two queues needs are required and depending on permissions those may be implicitly created by the application. The `<env>` placeholder shall be substituted with the actual name of the environment such as `stage` or `prod`.

- &lt;env>.aggregated.log.queue

### 1.5 Database

A database for the application must have been created.  It's recommended to use character set `utf8mb4` and case-sensitive collation. 

### 1.6 Access to Software Artifacts

Software artifacts are located at, and downloaded from:

* From Installing Client - https://build-inera.nordicmedtest.se/nexus/repository/releases/se/inera/intyg/rehabstod/
* From OpenShift Cluster - docker.drift.inera.se/intyg/

### 1.7 Access to OpenShift Cluster

The OpenShift user account must have the right permissions to process, create, delete and replace objects, and most certainly a VPN account and connection is required in order to access the OpenShift Cluster.

### 1.8 Client Software Tools

The installation client must have **git** and **oc** (OpenShift Client) installed and if a database schema migration is required then **java** (Java 8) and **tar** is required in order to execute the migration tool (liquibase runner).

Must have:

* git
* oc
* VPN Client (such as Cisco Any Connect) 

To run database migration tool:

* java
* tar

# 2. Installation Procedure

### 2.1 Installation Checklist

1. All Pre-Installation Requirements are fulfilled, se above
2. Check if a database migration is required
3. Ensure that the secrets env, certifikat and secret-envvar are up to date
4. Ensure that the configmap and configmap-envvar are up to date
5. Check that deployment works as expected 
6. Fine-tune memory settings for container and java process
7. Setup policies for number of replicas, auto-scaling and rolling upgrade strategy


### 2.2 Migrate Database Schema

Prior to any release that includes changes to the database schema, the operations provider must execute schema updates using the Liquibase runner tool provided in this section. 

_Please note: a complete database backup is recommended prior to run the database migration tool_

Replace `<version>` below with the actual application version.

Fetch the actual version of the tool, the example below runs `wget` to retrieve the package (tarball).

    > wget https://build-inera.nordicmedtest.se/nexus/repository/releases/se/inera/intyg/rehabstod/rehabstod-liquibase-runner/<version>/rehabstod-liquibase-runner-<version>.tar


Download onto a computer having Java installed and network access to the database and execute the runner.

    > tar xvf rehabstod-liquibase-runner-<version>.tar
    > cd rehabstod-liquibase-runner-<version>
    > bin/rehabstod-liquibase-runner --url=jdbc:mysql://<database-host>/<database-name> --username=<database_username> --password=<database_password> update


### 2.3 Get Source for Configuration


##### 2.3.1 Clone the repository

Clone repository and switch to the release branch specified in the release notes. Substitute `<name>` below with the actual release name, such as `2018-4`.
    
    > git clone https://github.com/sklintyg/rehabstod.git
    > git checkout release/<name>
    > cd devops/openshift
    
Note that we strongly recommend using a git account that has read-only (e.g. public) access to the repo.
    
##### 2.3.2 Log-in into the cluster

Use oc to login and select the actual project, e.g:

    > oc login https://path.to.cluster
    username: ******
    password: ******
    > oc project <name>

### 2.3.3 Ensure that the latest deployment template is installed

Download and install the latest _[deploytemplate-webapp.yaml](https://github.com/sklintyg/tools/blob/develop/devops/openshift/deploytemplate-webapp.yaml)_

Syntax to create or replace the template: 

	> oc [ create | replace ] -f deploytemplate-webapp.yaml

### 2.4 Update configuration placeholders

For security reasons, no secret properties or configuration may be checked into git. Thus, a number of placeholders needs to be replaced prior to creating or updating secrets and/or config maps.

Open _&lt;env>/secret-vars.yaml_ and replace `<value>` with expected values:

    ACTIVEMQ_BROKER_USERNAME: <value>
    ACTIVEMQ_BROKER_PASSWORD: <value>
    NTJP_WS_CERTIFICATE_PASSWORD: <value>
    NTJP_WS_KEY_MANAGER_PASSWORD: <value>
    NTJP_WS_TRUSTSTORE_PASSWORD: <value>
    SAKERHETSTJANST_SAML_KEYSTORE_PASSWORD: <value>
    SAKERHETSTJANST_SAML_TRUSTSTORE_PASSWORD: <value>
    SAKERHETSTJANST_WS_CERTIFICATE_PASSWORD: <value>
    SAKERHETSTJANST_WS_KEY_MANAGER_PASSWORD: <value>
    SAKERHETSTJANST_WS_TRUSTSTORE_PASSWORD: <value>

Open _&lt;env>/configmap-vars.yaml_ and replace `<value>` with expected values. You may also need to update name of keystore/truststore files as well as their type (JKS or PKCS12)

    WEBCERT_VIEW_URLTEMPLATE: <value>
    REDIS_HOST: <value>
    REDIS_PORT: <value>
    REDIS_SENTINEL_MASTER_NAME: <value>
    ACTIVEMQ_BROKER_URL: "tcp://<value>:<value>"
    PDL_LOGGING_QUEUE_NAME: <value>
    AGGREGATED_PDL_LOGGING_QUEUE_NAME: <value>
    DATABASE_PORT: "3306"
    DATABASE_SERVER: <value>
    NTJP_WS_CERTIFICATE_TYPE: <value>
    NTJP_WS_TRUSTSTORE_TYPE: <value>
    SAKERHETSTJANST_SAML_IDP_METADATA_URL: "https://idp2.acctest.sakerhetstjanst.inera.se:443/idp/saml"
    SAKERHETSTJANST_SAML_KEYSTORE_ALIAS: <value>
    SAKERHETSTJANST_SAML_KEYSTORE_FILE: "file://${certificate.folder}/<value>"
    SAKERHETSTJANST_SAML_TRUSTSTORE_FILE: "file://${certificate.folder}/<value>"
    NTJP_WS_CERTIFICATE_FILE: "${certificate.folder}/<value>"
    NTJP_WS_TRUSTSTORE_FILE: "${certificate.folder}/<value>"
    IT_WS_CERTIFICATE_FILE: <value>
    IT_WS_TRUSTSTORE_TYPE: [ "JKS" | "PKCS12" ]
   
Note: Other properties might be used to define a `<value>`. As an example is the path to certificates indicated by the `certificate.folder` property and the truststore file might be defined like:
 
	NTJP_WS_TRUSTSTORE_FILE: "${certificate.folder}/truststore.jks"
    
        
The _&lt;env>/config/recipients.json_ file may need to be updated with any new intyg recipients.
    
##### 2.4.1 Redis Sentinel Configuration

Redis sentinel needs at least three URL:s passed in order to work correctly. These are specified in the `REDIS_HOST` and `REDIS_PORT` variables respectively:

    REDIS_HOST: "host1;host2;host3"
    REDIS_PORT: "26379;26379;26379"
    REDIS_SENTINEL_MASTER_NAME: "master"
    
### 2.5 Prepare Certificates

The `<env>` placeholder shall be substituted with the actual name of the environment such as `stage` or `prod`.

Staging and Prod certificates are **never** committed to git. However, you may temporarily copy them to _&lt;env>/certifikat_ in order to install/update them. Typically, certificates have probably been installed separately. The important thing is that the deployment template **requires** a secret named: `rehabstod-<env>-certifikat` to be available in the OpenShift project. It will be mounted to _/opt/rehabstod-<env>/certifikat_ in the container file system.


### 2.6 Creating Config and Secrets
If you've finished updating the files above, it's now time to use **oc** to install them into OpenShift.
All commands must be executed from the same folder as this markdown file, i.e. _/rehabstod/devops/openshift_ 

Note: To delete an existing ConfigMap or Secret use the following syntax:

	> oc delete [ configmap | secret ] <name>

##### 2.6.1 Create environment variables for Secret and ConfigMap
From YAML-files, their names are hard-coded into the respective file

    > oc create -f <env>/configmap-vars.yaml
    > oc create -f <env>/secret-vars.yaml
    
##### 2.6.2 Create Secret and ConfigMap
Creates config map and secret from the contents of the _&lt;env>/env_ and _&lt;env>/config_ folders:

    > oc create configmap rehabstod-<env>-config --from-file=<env>/config/
    > oc create secret generic rehabstod-<env>-env --from-file=<env>/env/ --type=Opaque
    
##### 2.6.3 Create Secret with Certificates
If this hasn't been done previously, you may **temporarily** copy keystores into the _&lt;env>/certifikat_ folder and then install them into OpenShift using this command:

    > oc create secret generic rehabstod-<env>-certifikat --from-file=<env>/certifikat/ --type=Opaque

### 2.7 Deploy
We're all set for deploying the application. As stated in the pre-reqs, the "deploytemplate-webapp" must be installed in the OpenShift project.

**NOTE 1** You need to reference the correct docker image from the Nexus!!

**NOTE 2** Please specify the `DATABASE_NAME` actual MySQL database. Default is **rehabstod**.

Run the following command to create a deployment:

    > oc process deploytemplate-webapp \
        -p APP_NAME=rehabstod[-<env>] \
        -p IMAGE=docker.drift.inera.se/intyg/rehabstod-test:<version> \
        -p STAGE=<env> 
        -p DATABASE_NAME=<value> \
        -p HEALTH_URI=/inera-certificate/services \
        -o yaml | oc apply -f -
        
        
Alternatively, it's possible to use the deploytemplate-webapp file locally:

    > oc process -f deploytemplate-webapp.yaml -p APP_NAME=rehabstod[-<env>] ...

### 2.8 Verify
The pod(s) running rehabstod should become available within a few minutes use **oc** or Web Console to checkout the logs for progress:

	> oc logs dc/rehabstod-<env>

### 2.9 Routes
Rehabstöd should _only_ be accessible from inside of the OpenShift project using its _service_ name (e.g. http://rehabstod[-&lt;env>]:8080) and from Nationella tjänsteplattformen. E.g. take care when setting up an OpenShift route so the intygsyjanst servcie isn't publicly addressable from the Internet.

The security measures based on mutual TLS and PKI should nevertheless stop any attempts from unsolicited callers.
