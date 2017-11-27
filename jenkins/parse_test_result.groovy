import groovy.util.XmlParser

def parse(String basePath){
    def prefix = "${basePath}/build/test-results/integrationTest/TEST-jp.go.jpo.sa1s99.common.format.integration."
    def files = [
        new File("${prefix}HtmlToXmlForAmendApiIntegrationTest.xml"),
        new File("${prefix}HtmlToXmlApiIntegrationTest.xml")
    ]
    
    def list = files.collect{new XmlParser().parse(it)}.testcase.flatten()
    
    def groupedByExceptionClass = list.groupBy{ it.failure.@type[0]}
    groupedByExceptionClass.collect{ key, value -> "${key} : ${value.size()}"}.join("\n")
}

return this
