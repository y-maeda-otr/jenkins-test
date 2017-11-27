import groovy.util.XmlParser

def basePath = "build/test-results/integrationTest/TEST-jp.go.jpo.sa1s99.common.format.integration."
def files = [
    //new File("${basePath}HtmlToXmlForAmendApiIntegrationTest.xml"),
    new File("${basePath}HtmlToXmlApiIntegrationTest.xml")
]

def list = files.collect{new XmlParser().parse(it)}.testcase.flatten()

def groupedByExceptionClass = list.groupBy{ it.failure.@type[0]}

return groupedByExceptionClass.collect{ key, value -> "${key} : ${value.size()}"}.join("\n")
