/**
 * Extracts DB stored procedures metadata.
 */
class DBMetadata {

  List procedures(Map db, String catalog, String schemaPattern) {
    List rawData = readOrGenerateFile(out('rawData.json'),
      { getRawDataFromDB(db, catalog, schemaPattern) }) as List

    Map typeNames = readOrGenerateFile(resource('typeNames.json'), out('typeNames.json'),
      { generateTypeNames(rawData) }) as Map

    Map columnTypes = readJSON(resource('columnTypes.json')) as Map

    List cleanData = rawData.collect { row ->
      [
        name: cleanName(row['PROCEDURE_NAME']),
        columnName: row['COLUMN_NAME'].replace('@', ''),
        columnType: columnTypes[row['COLUMN_TYPE'] as String],
        typeName: typeNames[row['TYPE_NAME']]
      ]
    }
    writeJSON(out('cleanData.json'), cleanData)
    Map proceduresByNameAndColumnType = cleanData.groupBy([ { it['name'] }, { it['columnType'] } ])
    writeJSON(out('groupedData.json'), proceduresByNameAndColumnType)
    writeJSON(out('procedures.json'), procedures(proceduresByNameAndColumnType)) as List
  }

  List procedures(Map proceduresByNameAndColumnType) {
    proceduresByNameAndColumnType.collect { String name, Map parametersByColumnType ->
      [ name: name ] +
      [ signature: signature(name, parametersByColumnType.In) ] +
      parametersByColumnType.collectEntries { String columnType, List<Map> parameters ->
        [
          (columnType): parameters.collect { Map p ->
            [ columnName: p.columnName, typeName: p.typeName ]
          }
        ]
      }
    }
  }

  List getRawDataFromDB(Map db, String catalog, String schemaPattern) {
    List result
    groovy.sql.Sql.withInstance(db as Map) { groovy.sql.Sql sql ->
      result = getProceduresRawData(sql, catalog, schemaPattern)
    }
    return result
  }

  List getProceduresRawData(groovy.sql.Sql sql, String catalog, String schemaPattern) {
    debug("getProcedureColumns")
    java.sql.ResultSet rs = sql.connection.metaData.getProcedureColumns(catalog, schemaPattern, '%', null)
    debug("columns")
    List<String> columns = columns(rs.metaData)
    debug("build")
    List result = build( { rs.next() } ) { row(rs, columns) }
    return result
  }

  void debug(String msg) {
    println(new Date().format('HH:mm:ss.SSS') + " $msg")
  }

  Map generateTypeNames(List rawData) {
    rawData
      .groupBy( { it['TYPE_NAME'] } )
      .keySet()
      .collectEntries { typeName -> [(typeName): '?'] }
  }

  String signature(String name, List params) {
    "${name}(" + (params ?: []).collect { '?' }.join(',') + ")"
  }

  List<String> columns(java.sql.ResultSetMetaData rsmd) {
    int i = 1
    build( { i <= rsmd.columnCount } ) { rsmd.getColumnName(i++) }
  }

  Map<String, Object> row(rs, List<String> columns) {
    columns.collectEntries { String column -> [(column):rs.getObject(column)] }
  }

  List build(Closure exit, Closure element) {
    List list = []
    while (exit()) { list << element() }
    return list
  }

  File out(String fileName) { new File("build/out/$fileName") }

  File resource(String name) {
    new File(getClass().getResource(name)?.toURI() ?: '__THIS_FILE__DOES__NOT__EXIST__')
  }

  Object readJSON(File file) { new groovy.json.JsonSlurper().parseFile(file, 'UTF-8') }

  Object writeJSON(File file, Object data) {
    file.getParentFile().mkdirs();
    file.newWriter('UTF-8').withWriter { it << pretty(data) }
    return data
  }

  Object readOrGenerateFile(File fileIn, File fileOut = fileIn, Closure generate) {
    fileIn.exists() ? readJSON(fileIn) : writeJSON(fileOut, generate())
  }

  String pretty(Object object) { new groovy.json.JsonBuilder(object).toPrettyString() }

  String cleanName(String name) { name.split(';')[0] }

}
