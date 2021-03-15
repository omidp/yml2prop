# yml2prop
A Java lib to convert yml file to properties file and vice versa for spring boot applications mostly.

## How to build

```
mvn clean install -DskipTests=true
```


## How to use

```
Converter ymlToPropertiesConverter = new YmlToPropertiesConverter();
		ymlToPropertiesConverter.convert(ConverterTest.class.getResourceAsStream("/application.yml"), System.out);
    
    Converter ymlToPropertiesConverter = new PropertiesToYmlConverter();
		ymlToPropertiesConverter.convert(ConverterTest.class.getResourceAsStream("/application.properties"), System.out);
```
