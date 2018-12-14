# API Docs - v1.0.0-SNAPSHOT

## Sink

### report *<a target="_blank" href="https://wso2.github.io/siddhi/documentation/siddhi-4.0/#sink">(Sink)</a>*

<p style="word-wrap: break-word">Report sink can be used to publish (write) event data which is processed within siddhiinto reports.<br>Siddhi-io-report provides support to generate reports in PDF format.<br></p>

<span id="syntax" class="md-typeset" style="display: block; font-weight: bold;">Syntax</span>
```
@sink(type="report", outputpath="<STRING>", output.format="<STRING>", title="<STRING>", description="<STRING>", subtitle="<STRING>", template="<STRING>", dataset="<STRING>", header="<STRING>", footer="<STRING>", chart="<STRING>", chart.title="<STRING>", category="<STRING>", series="<STRING>", mode="<STRING>", queries="<STRING>", @map(...)))
```

<span id="query-parameters" class="md-typeset" style="display: block; color: rgba(0, 0, 0, 0.54); font-size: 12.8px; font-weight: bold;">QUERY PARAMETERS</span>
<table>
    <tr>
        <th>Name</th>
        <th style="min-width: 20em">Description</th>
        <th>Default Value</th>
        <th>Possible Data Types</th>
        <th>Optional</th>
        <th>Dynamic</th>
    </tr>
    <tr>
        <td style="vertical-align: top">outputpath</td>
        <td style="vertical-align: top; word-wrap: break-word">This parameter is used to specify the report path for data to be written.</td>
        <td style="vertical-align: top"></td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">No</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">output.format</td>
        <td style="vertical-align: top; word-wrap: break-word">This parameter is used to specify the format of the report generated. Only PDF, XLS, XLSX, CSV are supported.</td>
        <td style="vertical-align: top">PDF</td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">title</td>
        <td style="vertical-align: top; word-wrap: break-word">This parameter is used to specify the title of the report</td>
        <td style="vertical-align: top">none</td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">description</td>
        <td style="vertical-align: top; word-wrap: break-word">This parameter is used to specify the description of the report.</td>
        <td style="vertical-align: top">none</td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">subtitle</td>
        <td style="vertical-align: top; word-wrap: break-word">This parameter is used to specify the subtitle of the report</td>
        <td style="vertical-align: top">none</td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">template</td>
        <td style="vertical-align: top; word-wrap: break-word">This parameter is used to specify an external JRXML template path to generate the report. The given template will be filled and generate the report accordingly.</td>
        <td style="vertical-align: top">none</td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">dataset</td>
        <td style="vertical-align: top; word-wrap: break-word">This parameter is used to specify the dataset for the external template. This value can have a static stream attribute name or a dynamic value specified by '{}'eg:sink(type='report',dataset='{symbol}', @map(type='json'));define stream (symbol string, price float, volume long);</td>
        <td style="vertical-align: top">none</td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">header</td>
        <td style="vertical-align: top; word-wrap: break-word">This parameter is used to specify the header image for the report.</td>
        <td style="vertical-align: top">none</td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">footer</td>
        <td style="vertical-align: top; word-wrap: break-word">This parameter is used to specify the footer image for the report</td>
        <td style="vertical-align: top">none</td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">chart</td>
        <td style="vertical-align: top; word-wrap: break-word">Used to specify the chart type in the report. The value can be 'line', 'bar', 'pie', 'table'. The chart is added into the report according to the parameter value.</td>
        <td style="vertical-align: top">table</td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">chart.title</td>
        <td style="vertical-align: top; word-wrap: break-word">This parameter is used to specify the title of the chart. The title is added along with the chart.</td>
        <td style="vertical-align: top">none</td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">category</td>
        <td style="vertical-align: top; word-wrap: break-word">This parameter is used to specify the category variable for the chart defined. The value of this parameter will be taken as the X axis of the chart.</td>
        <td style="vertical-align: top">none</td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">series</td>
        <td style="vertical-align: top; word-wrap: break-word">This parameter is used to specify the series variable for the chart. The value of this parameter will be taken as the Y axis of the chart and it is necessary to provide  numerical value for this parameter.</td>
        <td style="vertical-align: top">none</td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">mode</td>
        <td style="vertical-align: top; word-wrap: break-word">This parameter is used to specify the series variable for the chart. The value of this parameter will be taken as the Y axis of the chart and it is necessary to provide  numerical value for this parameter.</td>
        <td style="vertical-align: top">stream</td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">queries</td>
        <td style="vertical-align: top; word-wrap: break-word">This parameter is used to specify the series variable for the chart. The value of this parameter will be taken as the Y axis of the chart and it is necessary to provide  numerical value for this parameter.</td>
        <td style="vertical-align: top">none</td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
</table>

<span id="examples" class="md-typeset" style="display: block; font-weight: bold;">Examples</span>
<span id="example-1" class="md-typeset" style="display: block; color: rgba(0, 0, 0, 0.54); font-size: 12.8px; font-weight: bold;">EXAMPLE 1</span>
```
 @sink(type='report',outputpath='/abc/example.pdf',@map(type='json'))define stream BarStream(symbol string, price float, volume long);
```
<p style="word-wrap: break-word"> Under above configuration, for an event chunck,a report of type PDF will be generated. There will be a table in the report.</p>

<span id="example-2" class="md-typeset" style="display: block; color: rgba(0, 0, 0, 0.54); font-size: 12.8px; font-weight: bold;">EXAMPLE 2</span>
```
 @sink(type='report',outputpath='/abc/{symbol}.pdf',@map(type='json'))define stream BarStream(symbol string, price float, volume long);
```
<p style="word-wrap: break-word"> Under above configuration, for an event chunck,a report of type PDF will be generated. The name of the report will be the first event value of the symbol parameter in the stream. There will be a table in the report.</p>

<span id="example-3" class="md-typeset" style="display: block; color: rgba(0, 0, 0, 0.54); font-size: 12.8px; font-weight: bold;">EXAMPLE 3</span>
```
 @sink(type='report',outputpath='/abc/example.pdf',description='This is a sample report for the report sink.',title='Sample Report',subtitle='Report sink sample',@map(type='json'))define stream BarStream(symbol string, price float, volume long);
```
<p style="word-wrap: break-word"> Under above configuration, for an event chunck,a report of type PDF will be generated. There will be a table in the report.The report title, description and subtitle will include the values specified as the parameters. The report will be generated in the given output path.</p>

<span id="example-4" class="md-typeset" style="display: block; color: rgba(0, 0, 0, 0.54); font-size: 12.8px; font-weight: bold;">EXAMPLE 4</span>
```
 @sink(type='report',outputpath='/abc/example.pdf',chart='Line',chart.title='Line chart for the sample report.',category='symbol',series='price',@map(type='json'))define stream BarStream(symbol string, price float, volume long);
```
<p style="word-wrap: break-word"> Under above configuration, for an event chunck,a report of type PDF will be generated.The report report will include a line chart with the specified chart title. The chart will be generated with the specified category and series. The report will be generated in the given output path.</p>

<span id="example-5" class="md-typeset" style="display: block; color: rgba(0, 0, 0, 0.54); font-size: 12.8px; font-weight: bold;">EXAMPLE 5</span>
```
 @sink(type='report', outputpath='/abc/example.pdf',mode='query',datasource.name='SAMPLE_DATASOURCE',queries="""[{"query":"SELECT * FROM SampleTable;","chart":"table"},@map(type='json'))
```
<p style="word-wrap: break-word"> Under above configuration, for an event trigger,a report of type PDF will be generated.The report report will include a table with the data from the RDBMS datasource specifies as 'datasource.name' and the data from the query as specified in 'queries'. The report will be saved in the given output path.</p>

<span id="example-6" class="md-typeset" style="display: block; color: rgba(0, 0, 0, 0.54); font-size: 12.8px; font-weight: bold;">EXAMPLE 6</span>
```
 @sink(type='report', outputpath='/abc/example.pdf',mode='query',datasource.name='SAMPLE_DATASOURCE',queries="""[{"query":"SELECT * FROM SampleTable;","chart":"table"},{"query":"SELECT Value, Age FROM SampleTable;","chart":"line","series":"Value","category":"Age","chart.title":"Test chart"}]""",
@map(type='json'))
```
<p style="word-wrap: break-word"> Under above configuration, for an event trigger,a report of type PDF will be generated. The will be two charts as per each RDBMS query. The datasource for both queries will be the value specified as 'datasource.name'. The first query will generate a table with the data from the query as specified in 'queries'. The second query will generate a line chart where the data will be taken from the second query as defined in the 'queries' parameter. The report will be saved in the given output path.</p>

