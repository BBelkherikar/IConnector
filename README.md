# IConnector

ManifoldCF with Postgresql:
Add the following properties in Path:\ManifoldCF\ManiFoldCf-Bin\apache-manifoldcf-2.5\example
properties.xml

<property name="org.apache.manifoldcf.databaseimplementationclass" value="org.apache.manifoldcf.core.database.DBInterfacePostgreSQL"/>
  <property name="org.apache.manifoldcf.dbsuperusername" value="postgres"/>
  <property name="org.apache.manifoldcf.dbsuperuserpassword" value="mcf123"/>
  <property name="org.apache.manifoldcf.postgresql.hostname" value="localhost"/>
  <property name="org.apache.manifoldcf.postgresql.port" value="5123"/>
