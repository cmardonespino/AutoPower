import spock.lang.Specification
import spock.lang.Shared
import groovy.sql.Sql

class AutoPowerSpec extends Specification {

  @Shared String autoPowerDB = 'TestDataPowerDB'

  @Shared
  Map<String, String> db = [
    driver:'com.microsoft.sqlserver.jdbc.SQLServerDriver',
    url:'jdbc:sqlserver://localhost:1433',
    user:'sa',
    password:'abcd.1234'
  ]

  def setupSpec() {
    Sql.withInstance(db as Map) { Sql sql ->
      sql.with {
        execute "CREATE DATABASE ${Sql.expand(autoPowerDB)}"
        execute "USE ${Sql.expand(autoPowerDB)}"
        execute '''
          CREATE PROCEDURE HelloWorld AS
           BEGIN
             SELECT 'Hello World' AS name
           END
        '''
        /*execute '''
          CREATE PROCEDURE HelloWorld2 @Name varchar AS
          BEGIN
            SELECT 'Hello World ' + @Name
          END
        '''*/
      }
    }
  }

  def 'create multiprotocol gateways from stored procedures'() {
    setup:
      String host = 'localhost'
      String domain = 'default'
      String user = 'admin'
      String pwd = 'admin'
      String pathFileMPG = 'build/out/services'
      String pathFileDS = 'src/test/resources/templates/data-source'
      String fileMPG = 'HelloWorld.zip'
      String fileDS = 'export.xml'
    when:
      List procedures = new DBMetadata().procedures(db, autoPowerDB, 'dbo')
      new MPGCreator().createMPGs(procedures)
      //new DataPowerConfigurationManager().importObject()
      new DataPowerConfigurationManager().importObject(host, domain, user, pwd, fileMPG, pathFileMPG)
      new DataPowerConfigurationManager().importObject(host, domain, user, pwd, fileDS, pathFileDS)
    then:
      procedures && procedures.size() > 0
      outputFileExistsAndNotEmpty('procedures.json')
      // TODO validate that templates are generated correctly
  }


  boolean outputFileExistsAndNotEmpty(String name) {
    new File("build/out/$name").with { exists() && size() > 0 }
  }
/*
  def cleanupSpec() {
    Sql.withInstance(db as Map) { Sql sql -> sql.execute "DROP DATABASE ${Sql.expand(autoPowerDB)}" }
  }
*/
  void debug(String msg) {
    println(new Date().format('HH:mm:ss.SSS') + " $msg")
  }

}
