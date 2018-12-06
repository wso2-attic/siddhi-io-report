/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.extension.siddhi.io.report.sink;

import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.extension.siddhi.io.report.util.ReportConstants;
import org.wso2.siddhi.core.SiddhiAppRuntime;
import org.wso2.siddhi.core.SiddhiManager;
import org.wso2.siddhi.core.event.Event;
import org.wso2.siddhi.core.exception.SiddhiAppCreationException;
import org.wso2.siddhi.core.stream.input.InputHandler;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ReportSink test case.
 */
public class TestCaseOfReportSink {
    private static final Logger LOGGER = Logger.getLogger(TestCaseOfReportSink.class);
    private ClassLoader classLoader;

    @BeforeClass
    public void init() {
        classLoader = TestCaseOfReportSink.class.getClassLoader();
        new File("TestReportURI").mkdir();
    }

    @Test
    public void reportSinkTest1() throws InterruptedException {
        LOGGER.info("----------------------------------------------------------------------------------------");
        LOGGER.info("ReportSink TestCase 1 - Configure siddhi to generate reports only using mandatory params");
        LOGGER.info("----------------------------------------------------------------------------------------");

        String streams = "" +
                "@App:name('TestSiddhiApp')" +
                "define stream FooStream(symbol string, price int, volume long, testval bool); " +
                "@sink(type='report',outputpath='testOut',@map(type='json')) " +
                "define stream BarStream (symbol string,price int, volume long, testval bool); ";

        String query = "" +
                "from FooStream " +
                "select * " +
                "insert into BarStream; ";

        SiddhiManager siddhiManager = new SiddhiManager();
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);
        InputHandler stockStream = siddhiAppRuntime.getInputHandler("FooStream");

        siddhiAppRuntime.start();

        Event testEvent1 = new Event();
        testEvent1.setData(new Object[]{"WSO2", 55.6f, 100L, true});

        Event testEvent2 = new Event();
        testEvent2.setData(new Object[]{"IBM", 57.8f, 100L, false});

        Event testEvent3 = new Event();
        testEvent3.setData(new Object[]{"GOOGLE", 50f, 100L, true});

        Event testEvent4 = new Event();
        testEvent4.setData(new Object[]{"WSO2", 55.6f, 100L, true});

        stockStream.send(new Event[]{testEvent1, testEvent2, testEvent3, testEvent4});

        File sink = new File("testOut.pdf");
        AssertJUnit.assertTrue(sink.exists());

        String file = classLoader.getResource("testOut.pdf").getFile();
        File testFile = new File(file);
        Assert.assertEquals(testFile.length() / 1024, sink.length() / 1024);
        siddhiAppRuntime.shutdown();
    }

    @Test
    public void reportSinkTest2() throws InterruptedException {
        LOGGER.info("----------------------------------------------------------------------------------------");
        LOGGER.info("ReportSink TestCase 2 - Configure siddhi to generate reports with report name given");
        LOGGER.info("----------------------------------------------------------------------------------------");

        String testReportName = "TestReport";
        String initialVolume = "100";

        String streams = "" +
                "@App:name('TestSiddhiApp')" +
                "define stream FooStream(symbol string, price float, volume long); " +
                "@sink(type='report',outputpath='" + testReportName + "{volume}',@map(type='json')) " +
                "define stream BarStream (symbol string,price float, volume long); ";

        String query = "" +
                "from FooStream " +
                "select * " +
                "insert into BarStream; ";

        SiddhiManager siddhiManager = new SiddhiManager();
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);
        InputHandler stockStream = siddhiAppRuntime.getInputHandler("FooStream");

        siddhiAppRuntime.start();

        Event testEvent1 = new Event();
        testEvent1.setData(new Object[]{"WSO2", 55.6f, 100L});

        Event testEvent2 = new Event();
        testEvent2.setData(new Object[]{"IBM", 57.678f, 200L});

        Event testEvent3 = new Event();
        testEvent3.setData(new Object[]{"GOOGLE", 50f, 300L});

        Event testEvent4 = new Event();
        testEvent4.setData(new Object[]{"WSO2", 55.6f, 400L});

        stockStream.send(new Event[]{testEvent1, testEvent2, testEvent3, testEvent4});

        File sink = new File(testReportName + initialVolume + ".pdf");
        AssertJUnit.assertTrue(sink.exists());
        siddhiAppRuntime.shutdown();
    }

    @Test
    public void reportSinkTest3() throws InterruptedException {
        LOGGER.info("---------------------------------------------------------------------------------------------");
        LOGGER.info("ReportSink TestCase 3 - Configure siddhi to generate reports with report path and name given.");
        LOGGER.info("---------------------------------------------------------------------------------------------");

        String testReportName = "TestReport";
        String testReportURI = "TestReportURI/";

        String streams = "" +
                "@App:name('TestSiddhiApp')" +
                "define stream FooStream(symbol string, price float, volume long); " +
                "@sink(type='report',outputpath='" + testReportURI + testReportName + "',@map(type='json')) " +
                "define stream BarStream (symbol string,price float, volume long); ";

        String query = "" +
                "from FooStream " +
                "select * " +
                "insert into BarStream; ";

        SiddhiManager siddhiManager = new SiddhiManager();
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);
        InputHandler stockStream = siddhiAppRuntime.getInputHandler("FooStream");

        siddhiAppRuntime.start();

        Event testEvent1 = new Event();
        testEvent1.setData(new Object[]{"WSO2", 55.6f, 100L});

        Event testEvent2 = new Event();
        testEvent2.setData(new Object[]{"IBM", 57.678f, 100L});

        Event testEvent3 = new Event();
        testEvent3.setData(new Object[]{"GOOGLE", 50f, 100L});

        Event testEvent4 = new Event();
        testEvent4.setData(new Object[]{"WSO2", 55.6f, 100L});

        stockStream.send(new Event[]{testEvent1, testEvent2, testEvent3, testEvent4});

        File sink = new File(testReportURI + testReportName + ".pdf");
        AssertJUnit.assertTrue(sink.exists());
        siddhiAppRuntime.shutdown();
    }

    @Test
    public void reportSinkTest4() throws InterruptedException {
        LOGGER.info("--------------------------------------------------------------------------------------------");
        LOGGER.info("ReportSink TestCase 4 - Configure siddhi to generate reports with invalid report path given.");
        LOGGER.info("--------------------------------------------------------------------------------------------");

        String testReportName = "TestReport";
        String testReportURI = "InvalidTestReportURI/";

        String streams = "" +
                "@App:name('TestSiddhiApp')" +
                "define stream FooStream(symbol string, price float, volume long); " +
                "@sink(type='report', outputpath='" + testReportURI + testReportName + "',@map" +
                "(type='json')) " +
                "define stream BarStream (symbol string,price float, volume long); ";

        String query = "" +
                "from FooStream " +
                "select * " +
                "insert into BarStream; ";

        SiddhiManager siddhiManager = new SiddhiManager();
        try {
            SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);
            InputHandler stockStream = siddhiAppRuntime.getInputHandler("FooStream");

            siddhiAppRuntime.start();

            Event testEvent1 = new Event();
            testEvent1.setData(new Object[]{"WSO2", 55.6f, 100L});

            Event testEvent2 = new Event();
            testEvent2.setData(new Object[]{"IBM", 57.678f, 100L});

            Event testEvent3 = new Event();
            testEvent3.setData(new Object[]{"GOOGLE", 50f, 100L});

            Event testEvent4 = new Event();
            testEvent4.setData(new Object[]{"WSO2", 55.6f, 100L});

            stockStream.send(new Event[]{testEvent1, testEvent2, testEvent3, testEvent4});

            File sink = new File(testReportURI + testReportName + ".pdf");
            AssertJUnit.assertTrue(sink.exists());
            siddhiAppRuntime.shutdown();
        } catch (SiddhiAppCreationException e) {
            Assert.assertEquals(e.getMessageWithOutContext(), "In 'report' sink of siddhi app TestSiddhiApp " +
                    testReportURI.substring(0, testReportURI.lastIndexOf("/")) + " does not exists. " +
                    "outputpath should be a valid path");
        }
    }

    @Test
    public void reportSinkTest5() throws InterruptedException {
        LOGGER.info("-------------------------------------------------------------");
        LOGGER.info("ReportSink TestCase 5 - Generate reports with template given.");
        LOGGER.info("-------------------------------------------------------------");

        String testReportName = "TestTemplateReportNow";
        String testReportURI = "TestReportURI/";
        String testTemplatePath = classLoader.getResource("fromResultsetData.jrxml").getFile();

        String streams = "" +
                "@App:name('TestSiddhiApp')" +
                "define stream FooStream(symbol string, price float, volume long); " +
                "@sink(type='report', outputpath='" + testReportURI +
                testReportName + "{symbol}', template='" + testTemplatePath + "',@map(type='json')) " +
                "define stream BarStream (symbol string,price float, volume long); ";

        String query = "" +
                "from FooStream " +
                "select * " +
                "insert into BarStream; ";

        SiddhiManager siddhiManager = new SiddhiManager();
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);
        InputHandler stockStream = siddhiAppRuntime.getInputHandler("FooStream");

        siddhiAppRuntime.start();

        Event testEvent1 = new Event();
        testEvent1.setData(new Object[]{"WSO2", 55.6f, 100L});

        Event testEvent2 = new Event();
        testEvent2.setData(new Object[]{"IBM", 57.678f, 100L});

        Event testEvent3 = new Event();
        testEvent3.setData(new Object[]{"GOOGLE", 50f, 100L});

        Event testEvent4 = new Event();
        testEvent4.setData(new Object[]{"WSO2", 55.6f, 100L});

        stockStream.send(new Event[]{testEvent1, testEvent2, testEvent3, testEvent4});

        File sink = new File(testReportURI + testReportName + "WSO2.pdf");
        AssertJUnit.assertTrue(sink.exists());
        siddhiAppRuntime.shutdown();
    }

    @Test
    public void reportSinkTest6() throws InterruptedException {
        LOGGER.info("---------------------------------------------------------------------");
        LOGGER.info("ReportSink TestCase 6 - Generate reports with invalid template given.");
        LOGGER.info("---------------------------------------------------------------------");

        String testReportName = "TestTemplateReport";
        String testReportURI = "TestReportURI/";
        String invalidTemplatePath = "testInvalidTemplate.jrxml";

        String streams = "" +
                "@App:name('TestSiddhiApp')" +
                "define stream FooStream(symbol string, price float, volume long); " +
                "@sink(type='report',outputpath='" + testReportURI + testReportName + "'," +
                "template='" + invalidTemplatePath + "',@map(type='json')) " +
                "define stream BarStream (symbol string,price float, volume long); ";

        String query = "" +
                "from FooStream " +
                "select * " +
                "insert into BarStream; ";

        SiddhiManager siddhiManager = new SiddhiManager();
        try {
            SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);
            InputHandler stockStream = siddhiAppRuntime.getInputHandler("FooStream");

            siddhiAppRuntime.start();

            Event testEvent1 = new Event();
            testEvent1.setData(new Object[]{"WSO2", 55.6f, 100L});

            Event testEvent2 = new Event();
            testEvent2.setData(new Object[]{"IBM", 57.678f, 100L});

            Event testEvent3 = new Event();
            testEvent3.setData(new Object[]{"GOOGLE", 50f, 100L});

            Event testEvent4 = new Event();
            testEvent4.setData(new Object[]{"WSO2", 55.6f, 100L});

            stockStream.send(new Event[]{testEvent1, testEvent2, testEvent3, testEvent4});

            File sink = new File(testReportURI + testReportName + ".pdf");
            AssertJUnit.assertTrue(sink.exists());
            siddhiAppRuntime.shutdown();
        } catch (SiddhiAppCreationException e) {
            Assert.assertEquals(e.getMessageWithOutContext(), "In 'report' sink of siddhi app TestSiddhiApp " +
                    invalidTemplatePath + " does not exists. template should be a valid path");
        }
    }

    @Test
    public void reportSinkTest7() throws InterruptedException {
        LOGGER.info("------------------------------------------------------------------------------");
        LOGGER.info("ReportSink TestCase 7 - Generate reports with invalid header image given.");
        LOGGER.info("------------------------------------------------------------------------------");

        String testReportName = "TestHeaderReport";
        String testReportURI = "TestReportURI/";
        String invalidHeaderPath = "invalidHeaderImage.png";

        String streams = "" +
                "@App:name('TestSiddhiApp')" +
                "define stream FooStream(symbol string, price float, volume long); " +
                "@sink(type='report',outputpath='" + testReportURI + testReportName + "'," +
                "header='" + invalidHeaderPath + "',@map(type='json')) " +
                "define stream BarStream (symbol string,price float, volume long); ";

        String query = "" +
                "from FooStream " +
                "select * " +
                "insert into BarStream; ";

        SiddhiManager siddhiManager = new SiddhiManager();
        try {
            SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);
            InputHandler stockStream = siddhiAppRuntime.getInputHandler("FooStream");

            siddhiAppRuntime.start();

            Event testEvent1 = new Event();
            testEvent1.setData(new Object[]{"WSO2", 55.6f, 100L});

            Event testEvent2 = new Event();
            testEvent2.setData(new Object[]{"IBM", 57.678f, 100L});

            Event testEvent3 = new Event();
            testEvent3.setData(new Object[]{"GOOGLE", 50f, 100L});

            Event testEvent4 = new Event();
            testEvent4.setData(new Object[]{"WSO2", 55.6f, 100L});

            stockStream.send(new Event[]{testEvent1, testEvent2, testEvent3, testEvent4});

            File sink = new File(testReportURI + testReportName + ".pdf");
            AssertJUnit.assertTrue(sink.exists());
            siddhiAppRuntime.shutdown();
        } catch (SiddhiAppCreationException e) {
            Assert.assertEquals(e.getMessageWithOutContext(), "In 'report' sink of siddhi app TestSiddhiApp " +
                    invalidHeaderPath + " does not exists. header should be a valid path");
        }
    }

    @Test
    public void reportSinkTest8() throws InterruptedException {
        LOGGER.info("-----------------------------------------------------------------------");
        LOGGER.info("ReportSink TestCase 8 - Generate reports with invalid chart type given.");
        LOGGER.info("-----------------------------------------------------------------------");

        String testReportName = "TestChartReport";
        String testReportURI = "TestReportURI/";
        String invalidChartType = "histogram";

        String streams = "" +
                "@App:name('TestSiddhiApp')" +
                "define stream FooStream(symbol string, price float, volume long); " +
                "@sink(type='report',outputpath='" + testReportURI + testReportName + "'," +
                "chart='" + invalidChartType + "',@map(type='json')) " +
                "define stream BarStream (symbol string,price float, volume long); ";

        String query = "" +
                "from FooStream " +
                "select * " +
                "insert into BarStream; ";

        SiddhiManager siddhiManager = new SiddhiManager();
        try {
            SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);
            InputHandler stockStream = siddhiAppRuntime.getInputHandler("FooStream");

            siddhiAppRuntime.start();

            Event testEvent1 = new Event();
            testEvent1.setData(new Object[]{"WSO2", 55.6f, 100L});

            Event testEvent2 = new Event();
            testEvent2.setData(new Object[]{"IBM", 57.678f, 100L});

            Event testEvent3 = new Event();
            testEvent3.setData(new Object[]{"GOOGLE", 50f, 100L});

            Event testEvent4 = new Event();
            testEvent4.setData(new Object[]{"WSO2", 55.6f, 100L});

            stockStream.send(new Event[]{testEvent1, testEvent2, testEvent3, testEvent4});

            File sink = new File(testReportURI + testReportName + ".pdf");
            AssertJUnit.assertTrue(sink.exists());
            siddhiAppRuntime.shutdown();
        } catch (SiddhiAppCreationException e) {
            Assert.assertEquals(e.getMessageWithOutContext(), "In 'report' sink of siddhi app TestSiddhiApp "
                    + invalidChartType + " is not a valid chart type. Only table,line,bar,pie charts are supported.");
        }
    }

    @Test
    public void reportSinkTest9() throws InterruptedException {
        LOGGER.info("-----------------------------------------------------------------------");
        LOGGER.info("ReportSink TestCase 9 - Generate reports with invalid series type given.");
        LOGGER.info("-----------------------------------------------------------------------");

        String testReportName = "TestChartReport";
        String testReportURI = "TestReportURI/";
        String testChartType = "line";
        String invalidSeries = "symbol";

        String streams = "" +
                "@App:name('TestSiddhiApp')" +
                "define stream FooStream(symbol string, price float, volume long); " +
                "@sink(type='report',outputpath='" + testReportURI + testReportName + "'," +
                "chart='" + testChartType + "',series='" + invalidSeries + "',@map(type='json')) " +
                "define stream BarStream (symbol string,price float, volume long); ";

        String query = "" +
                "from FooStream " +
                "select * " +
                "insert into BarStream; ";

        SiddhiManager siddhiManager = new SiddhiManager();
        try {
            SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);
            InputHandler stockStream = siddhiAppRuntime.getInputHandler("FooStream");

            siddhiAppRuntime.start();

            Event testEvent1 = new Event();
            testEvent1.setData(new Object[]{"WSO2", 55.6f, 100L});

            Event testEvent2 = new Event();
            testEvent2.setData(new Object[]{"IBM", 57.678f, 100L});

            Event testEvent3 = new Event();
            testEvent3.setData(new Object[]{"GOOGLE", 50f, 100L});

            Event testEvent4 = new Event();
            testEvent4.setData(new Object[]{"WSO2", 55.6f, 100L});

            stockStream.send(new Event[]{testEvent1, testEvent2, testEvent3, testEvent4});

            File sink = new File(testReportURI + testReportName + ".pdf");
            AssertJUnit.assertTrue(sink.exists());
            siddhiAppRuntime.shutdown();
        } catch (SiddhiAppCreationException e) {
            Assert.assertEquals(e.getMessageWithOutContext(), "In 'report' sink of siddhi app TestSiddhiApp "
                    + invalidSeries + "is invalid. Provide a numeric series column.");
        }
    }

    @Test
    public void reportSinkTest10() throws InterruptedException {
        LOGGER.info("-------------------------------------------------------------------------------");
        LOGGER.info("ReportSink TestCase 10 - Generate reports with series and category types given.");
        LOGGER.info("-------------------------------------------------------------------------------");

        String testReportName = "TestChartSeriesCategoryReport";
        String testReportURI = "TestReportURI/";
        String testChartType = "line";
        String testCategory = "symbol";
        String testSeries = "price";

        String streams = "" +
                "@App:name('TestSiddhiApp')" +
                "define stream FooStream(symbol string, price float, volume long); " +
                "@sink(type='report',outputpath='" + testReportURI + testReportName + "'," +
                "chart='" + testChartType + "',series='" + testSeries + "',category='" + testCategory + "',@map" +
                "(type='json')) " +
                "define stream BarStream (symbol string,price float, volume long); ";

        String query = "" +
                "from FooStream " +
                "select * " +
                "insert into BarStream; ";

        SiddhiManager siddhiManager = new SiddhiManager();

        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);
        InputHandler stockStream = siddhiAppRuntime.getInputHandler("FooStream");

        siddhiAppRuntime.start();

        Event testEvent1 = new Event();
        testEvent1.setData(new Object[]{"WSO2", 55.6f, 100L});

        Event testEvent2 = new Event();
        testEvent2.setData(new Object[]{"IBM", 57.678f, 100L});

        Event testEvent3 = new Event();
        testEvent3.setData(new Object[]{"GOOGLE", 50f, 100L});

        Event testEvent4 = new Event();
        testEvent4.setData(new Object[]{"WSO2", 55.6f, 100L});

        stockStream.send(new Event[]{testEvent1, testEvent2, testEvent3, testEvent4});

        File sink = new File(testReportURI + testReportName + ".pdf");
        AssertJUnit.assertTrue(sink.exists());
        siddhiAppRuntime.shutdown();

    }

    @Test
    public void reportSinkTest11() throws InterruptedException {
        LOGGER.info("--------------------------------------------------------------");
        LOGGER.info("ReportSink TestCase 11 - Generate reports with a single event.");
        LOGGER.info("--------------------------------------------------------------");

        String testReportName = "TestSingleEventReport";
        String testReportURI = "TestReportURI/";
        String testChartType = "line";
        String testCategory = "symbol";
        String testSeries = "price";

        String streams = "" +
                "@App:name('TestSiddhiApp')" +
                "define stream FooStream(symbol string, price float, volume long); " +
                "@sink(type='report',outputpath='" + testReportURI + testReportName + "'," +
                "chart='" + testChartType + "',series='" + testSeries + "',category='" + testCategory + "',@map" +
                "(type='json')) " +
                "define stream BarStream (symbol string,price float, volume long); ";

        String query = "" +
                "from FooStream " +
                "select * " +
                "insert into BarStream; ";

        SiddhiManager siddhiManager = new SiddhiManager();

        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);
        InputHandler stockStream = siddhiAppRuntime.getInputHandler("FooStream");

        siddhiAppRuntime.start();

        Event testEvent1 = new Event();
        testEvent1.setData(new Object[]{"WSO2", 55.6f, 100L});

        Event testEvent2 = new Event();
        testEvent2.setData(new Object[]{"IBM", 57.678f, 100L});

        Event testEvent3 = new Event();
        testEvent3.setData(new Object[]{"GOOGLE", 50f, 100L});

        Event testEvent4 = new Event();
        testEvent4.setData(new Object[]{"WSO2", 55.6f, 100L});

        stockStream.send(new Object[]{"WSO2", 55.6f, 100L});

        File sink = new File(testReportURI + testReportName + ".pdf");
        AssertJUnit.assertTrue(sink.exists());
        siddhiAppRuntime.shutdown();
    }

    @Test
    public void reportSinkTest12() throws InterruptedException {
        LOGGER.info("-------------------------------------------------------------------------------");
        LOGGER.info("ReportSink TestCase 12 - Generate reports with dynamic variable in outputpath");
        LOGGER.info("-------------------------------------------------------------------------------");

        String testReportName = "TestReport";
        String dynamicValue = "55.6";

        String streams = "" +
                "@App:name('TestSiddhiApp')" +
                "define stream FooStream(symbol string, price float, volume long); " +
                "@sink(type='report',outputpath='" + testReportName + "{price}',@map(type='json')) " +
                "define stream BarStream (symbol string,price float, volume long); ";

        String query = "" +
                "from FooStream " +
                "select * " +
                "insert into BarStream; ";

        SiddhiManager siddhiManager = new SiddhiManager();
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);
        InputHandler stockStream = siddhiAppRuntime.getInputHandler("FooStream");

        siddhiAppRuntime.start();

        Event testEvent1 = new Event();
        testEvent1.setData(new Object[]{"WSO2", 55.6f, 100L});

        Event testEvent2 = new Event();
        testEvent2.setData(new Object[]{"IBM", 57.678f, 200L});

        Event testEvent3 = new Event();
        testEvent3.setData(new Object[]{"GOOGLE", 50f, 300L});

        Event testEvent4 = new Event();
        testEvent4.setData(new Object[]{"WSO2", 55.6f, 400L});

        stockStream.send(new Event[]{testEvent1, testEvent2, testEvent3, testEvent4});

        File sink = new File(testReportName + dynamicValue + ".pdf");
        AssertJUnit.assertTrue(sink.exists());
        siddhiAppRuntime.shutdown();
    }

    @Test
    public void reportSinkTest13() throws InterruptedException {
        LOGGER.info("--------------------------------------------------------------------------------------");
        LOGGER.info("ReportSink TestCase 13 - Generate reports with invalid dynamic variable in outputpath");
        LOGGER.info("--------------------------------------------------------------------------------------");

        String testReportName = "TestReport";
        String testDynamicReportName = "invalidName";

        String streams = "" +
                "@App:name('TestSiddhiApp')" +
                "define stream FooStream(symbol string, price float, volume long); " +
                "@sink(type='report',outputpath='" + testReportName + "{" + testDynamicReportName + "'," +
                "@map(type='json')) " +
                "define stream BarStream (symbol string,price float, volume long); ";

        String query = "" +
                "from FooStream " +
                "select * " +
                "insert into BarStream; ";

        SiddhiManager siddhiManager = new SiddhiManager();

        Event testEvent1 = new Event();
        testEvent1.setData(new Object[]{"WSO2", 55.6f, 100L});

        Event testEvent2 = new Event();
        testEvent2.setData(new Object[]{"IBM", 57.678f, 200L});

        Event testEvent3 = new Event();
        testEvent3.setData(new Object[]{"GOOGLE", 50f, 300L});

        Event testEvent4 = new Event();
        testEvent4.setData(new Object[]{"WSO2", 55.6f, 400L});

        try {
            SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);
            InputHandler stockStream = siddhiAppRuntime.getInputHandler("FooStream");
            siddhiAppRuntime.start();
            stockStream.send(new Event[]{testEvent1, testEvent2, testEvent3, testEvent4});
            siddhiAppRuntime.shutdown();
        } catch (SiddhiAppCreationException e) {
            Assert.assertEquals(e.getMessageWithOutContext(), "In 'report' sink of siddhi app TestSiddhiApp Invalid " +
                    "Property '" + testDynamicReportName + "'. No such parameter in the stream definition");
        }
    }

    @Test
    public void reportSinkTest14() throws InterruptedException {
        LOGGER.info("-----------------------------------------------------------------------------------------------");
        LOGGER.info("ReportSink TestCase 14 - Generate reports with invalid syntax for dynamic values in outputpath");
        LOGGER.info("-----------------------------------------------------------------------------------------------");

        String testReportName = "TestReport";
        String testDynamicReportName = "volume";

        String streams = "" +
                "@App:name('TestSiddhiApp')" +
                "define stream FooStream(symbol string, price float, volume long); " +
                "@sink(type='report',outputpath='" + testReportName + "{" + testDynamicReportName + "}'," +
                "@map(type='json')) " +
                "define stream BarStream (symbol string,price float, volume long); ";

        String query = "" +
                "from FooStream " +
                "select * " +
                "insert into BarStream; ";

        SiddhiManager siddhiManager = new SiddhiManager();

        Event testEvent1 = new Event();
        testEvent1.setData(new Object[]{"WSO2", 55.6f, 100L});

        Event testEvent2 = new Event();
        testEvent2.setData(new Object[]{"IBM", 57.678f, 200L});

        Event testEvent3 = new Event();
        testEvent3.setData(new Object[]{"GOOGLE", 50f, 300L});

        Event testEvent4 = new Event();
        testEvent4.setData(new Object[]{"WSO2", 55.6f, 400L});

        try {
            SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);
            InputHandler stockStream = siddhiAppRuntime.getInputHandler("FooStream");
            siddhiAppRuntime.start();
            stockStream.send(new Event[]{testEvent1, testEvent2, testEvent3, testEvent4});
            siddhiAppRuntime.shutdown();
        } catch (SiddhiAppCreationException e) {
            Assert.assertEquals(e.getMessageWithOutContext(), "In 'report' sink of siddhi app TestSiddhiApp Invalid " +
                    "Property '" + testDynamicReportName + "'. No such parameter in the stream definition");
        }
    }

    @Test
    public void reportSinkTest15() throws InterruptedException {
        LOGGER.info("--------------------------------------------------------------------------------------");
        LOGGER.info("ReportSink TestCase 15 - Generate reports with multiple datasets for a given template.");
        LOGGER.info("--------------------------------------------------------------------------------------");

        String testReportName = "TestReportWithMultipleDatasets";
        String datasourceName1 = "TableDataSource";
        String datasourceName2 = "OtherTableDataSource";
        String testTemplate = classLoader.getResource("fromResultsetDataMultiple.jrxml").getFile();

        String streams = "" +
                "@App:name('TestSiddhiApp')" +
                "define stream FooStream(symbol string, price float, volume long); " +
                "@sink(type='report',outputpath='" + testReportName + "', template='" + testTemplate + "', " +
                "@map(type='json')) " +
                "define stream BarStream (datasource string,symbol string,price float, volume long); ";

        String query = "" +
                "from FooStream " +
                "select ifThenElse(volume>200,'" + datasourceName1 + "','" + datasourceName2 + "') as datasource, " +
                "symbol,price,volume " +
                "insert into BarStream; ";

        SiddhiManager siddhiManager = new SiddhiManager();

        Event testEvent1 = new Event();
        testEvent1.setData(new Object[]{"WSO2", 55.6f, 100L});

        Event testEvent2 = new Event();
        testEvent2.setData(new Object[]{"IBM", 57.678f, 200L});

        Event testEvent3 = new Event();
        testEvent3.setData(new Object[]{"GOOGLE", 50f, 300L});

        Event testEvent4 = new Event();
        testEvent4.setData(new Object[]{"WSO2", 55.6f, 400L});

        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);
        InputHandler stockStream = siddhiAppRuntime.getInputHandler("FooStream");
        siddhiAppRuntime.start();
        stockStream.send(new Event[]{testEvent1, testEvent2, testEvent3, testEvent4});
        siddhiAppRuntime.shutdown();
    }

    @Test
    public void reportSinkTest16() throws InterruptedException {
        LOGGER.info("--------------------------------------------------------------------------------------------");
        LOGGER.info("ReportSink TestCase 16 - Generate reports with multiple dynamic datasets for given template.");
        LOGGER.info("--------------------------------------------------------------------------------------------");

        String testReportName = "TestReportWithMultipleDatasetsDynamic";
        String datasourceName1 = "TableDataSource";
        String datasourceName2 = "OtherTableDataSource";
        String testTemplate = classLoader.getResource("fromResultsetDataMultiple.jrxml").getFile();

        String streams = "" +
                "@App:name('TestSiddhiApp')" +
                "define stream FooStream(symbol string, price float, volume long); " +
                "@sink(type='report',outputpath='" + testReportName + "', template='" + testTemplate + "', " +
                "dataset='datasource'," +
                "@map(type='json')) " +
                "define stream BarStream (datasource string,symbol string,price float, volume long); ";

        String query = "" +
                "from FooStream " +
                "select ifThenElse(volume>200,'" + datasourceName1 + "','" + datasourceName2 + "') as datasource, " +
                "symbol,price,volume " +
                "insert into BarStream; ";

        SiddhiManager siddhiManager = new SiddhiManager();

        Event testEvent1 = new Event();
        testEvent1.setData(new Object[]{"WSO2", 55.6f, 100L});

        Event testEvent2 = new Event();
        testEvent2.setData(new Object[]{"IBM", 57.678f, 200L});

        Event testEvent3 = new Event();
        testEvent3.setData(new Object[]{"GOOGLE", 50f, 300L});

        Event testEvent4 = new Event();
        testEvent4.setData(new Object[]{"WSO2", 55.6f, 400L});

        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);
        InputHandler stockStream = siddhiAppRuntime.getInputHandler("FooStream");
        siddhiAppRuntime.start();
        stockStream.send(new Event[]{testEvent1, testEvent2, testEvent3, testEvent4});
        siddhiAppRuntime.shutdown();

    }

    @Test
    public void reportSinkTest17() throws InterruptedException {
        LOGGER.info("----------------------------------------------------------------------------------------------");
        LOGGER.info("ReportSink TestCase 17 - Generate reports with multiple dynamic dataset values given template.");
        LOGGER.info("----------------------------------------------------------------------------------------------");

        String testReportName = "TestReportWithMultipleDatasetsDynamicValues";
        String datasourceName1 = "datasource1";
        String datasourceName2 = "datasource2";
        String testTemplate = classLoader.getResource("fromResultsetDataMultipleDynamicValue.jrxml").getFile();

        String streams = "" +
                "@App:name('TestSiddhiApp')" +
                "define stream FooStream(symbol string, price float, volume long); " +
                "@sink(type='report',outputpath='" + testReportName + "', template='" + testTemplate + "', " +
                "dataset = '{symbol}' ," +
                "@map(type='json')) " +
                "define stream BarStream (datasource string,symbol string,price float, volume long); ";

        String query = "" +
                "from FooStream " +
                "select ifThenElse(volume>200,'" + datasourceName1 + "','" + datasourceName2 + "') as datasource, " +
                "symbol,price,volume " +
                "insert into BarStream; ";

        SiddhiManager siddhiManager = new SiddhiManager();

        Event testEvent1 = new Event();
        testEvent1.setData(new Object[]{"WSO2", 55.6f, 100L});

        Event testEvent2 = new Event();
        testEvent2.setData(new Object[]{"IBM", 57.678f, 200L});

        Event testEvent3 = new Event();
        testEvent3.setData(new Object[]{"WSO2", 50f, 300L});

        Event testEvent4 = new Event();
        testEvent4.setData(new Object[]{"WSO2", 55.6f, 400L});

        Event testEvent5 = new Event();
        testEvent5.setData(new Object[]{"IBM", 50f, 300L});

        Event testEvent6 = new Event();
        testEvent6.setData(new Object[]{"IBM", 55.6f, 400L});

        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);
        InputHandler stockStream = siddhiAppRuntime.getInputHandler("FooStream");
        siddhiAppRuntime.start();
        stockStream.send(new Event[]{testEvent1, testEvent2, testEvent3, testEvent4, testEvent5, testEvent6});
        siddhiAppRuntime.shutdown();

    }

    @Test
    public void reportSinkTest18() throws InterruptedException {
        LOGGER.info("-----------------------------------------------------------------------------");
        LOGGER.info("ReportSink TestCase 18 - Generate reports with dynamic variable in outputpath");
        LOGGER.info("-----------------------------------------------------------------------------");

        String testReportName = "TestReport";
        String testReportDir = "TestReportURI/";
        String testDynamicReportName = "symbol";

        String streams = "" +
                "@App:name('TestSiddhiApp')" +
                "define stream FooStream(symbol string, price float, volume long); " +
                "@sink(type='report'," +
                "outputpath='" + testReportDir + testReportName + "{" + testDynamicReportName + "}.html'," +
                "@map(type='json')) " +
                "define stream BarStream (symbol string,price float, volume long); ";

        String query = "" +
                "from FooStream " +
                "select * " +
                "insert into BarStream; ";

        SiddhiManager siddhiManager = new SiddhiManager();

        Event testEvent1 = new Event();
        testEvent1.setData(new Object[]{"WSO2", 55.6f, 100L});

        Event testEvent2 = new Event();
        testEvent2.setData(new Object[]{"IBM", 57.678f, 200L});

        Event testEvent3 = new Event();
        testEvent3.setData(new Object[]{"GOOGLE", 50f, 300L});

        Event testEvent4 = new Event();
        testEvent4.setData(new Object[]{"WSO2", 55.6f, 400L});

        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);
        InputHandler stockStream = siddhiAppRuntime.getInputHandler("FooStream");
        siddhiAppRuntime.start();
        stockStream.send(new Event[]{testEvent1, testEvent2, testEvent3, testEvent4});
        File sink = new File(testReportDir + testReportName + ".pdf");
        AssertJUnit.assertTrue(sink.exists());
        siddhiAppRuntime.shutdown();

    }

    @Test
    public void reportSinkTest19() throws InterruptedException {
        LOGGER.info("----------------------------------------------------------------------------");
        LOGGER.info("ReportSink TestCase 19 - Generate reports without series and category types.");
        LOGGER.info("----------------------------------------------------------------------------");

        String testReportName = "TestEmptrySeriesCatChartReport";
        String testReportURI = "TestReportURI/";
        String testChartType = "line";

        String streams = "" +
                "@App:name('TestSiddhiApp')" +
                "define stream FooStream(symbol string, price float, volume long); " +
                "@sink(type='report',outputpath='" + testReportURI + testReportName + "'," +
                "chart='" + testChartType + "',@map(type='json')) " +
                "define stream BarStream (symbol string,price float, volume long); ";

        String query = "" +
                "from FooStream " +
                "select * " +
                "insert into BarStream; ";

        SiddhiManager siddhiManager = new SiddhiManager();

        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);
        InputHandler stockStream = siddhiAppRuntime.getInputHandler("FooStream");

        siddhiAppRuntime.start();

        Event testEvent1 = new Event();
        testEvent1.setData(new Object[]{"WSO2", 55.6f, 100L});

        Event testEvent2 = new Event();
        testEvent2.setData(new Object[]{"IBM", 57.678f, 100L});

        Event testEvent3 = new Event();
        testEvent3.setData(new Object[]{"GOOGLE", 50f, 100L});

        Event testEvent4 = new Event();
        testEvent4.setData(new Object[]{"WSO2", 55.6f, 100L});

        stockStream.send(new Event[]{testEvent1, testEvent2, testEvent3, testEvent4});

        File sink = new File(testReportURI + testReportName + ".pdf");
        AssertJUnit.assertTrue(sink.exists());
        siddhiAppRuntime.shutdown();
    }

    @Test
    public void reportSinkTest20() {
        LOGGER.info("------------------------------------------------------------------------------------------");
        LOGGER.info("ReportSink TestCase 20 - Generate reports for all chart types without series and category.");
        LOGGER.info("------------------------------------------------------------------------------------------");

        String testReportURI = "TestReportURI/";
        String[] testChartTypes = new String[]{"Pie", "Bar", "Line"};
        AtomicInteger count = new AtomicInteger();

        Arrays.stream(testChartTypes).forEach(chartType -> {
            String testReportName = "Test" + chartType + "ChartReport";

            String streams = "" +
                    "@App:name('TestSiddhiApp')" +
                    "define stream FooStream(symbol string, price float, volume long); " +
                    "@sink(type='report',outputpath='" + testReportURI + testReportName + "'," +
                    "chart='" + chartType + "',@map(type='json')) " +
                    "define stream BarStream (symbol string,price float, volume long); ";

            String query = "" +
                    "from FooStream " +
                    "select * " +
                    "insert into BarStream; ";

            SiddhiManager siddhiManager = new SiddhiManager();

            SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);
            InputHandler stockStream = siddhiAppRuntime.getInputHandler("FooStream");

            siddhiAppRuntime.start();

            Event testEvent1 = new Event();
            testEvent1.setData(new Object[]{"WSO2", 55.6f, 100L});

            Event testEvent2 = new Event();
            testEvent2.setData(new Object[]{"IBM", 57.678f, 100L});

            Event testEvent3 = new Event();
            testEvent3.setData(new Object[]{"GOOGLE", 50f, 100L});

            try {
                stockStream.send(new Event[]{testEvent1, testEvent2, testEvent3});
            } catch (InterruptedException e) {
                LOGGER.error(e);
            }

            File sink = new File(testReportURI + testReportName + ".pdf");
            if (sink.exists()) {
                count.incrementAndGet();
            }
            siddhiAppRuntime.shutdown();
        });
        AssertJUnit.assertEquals(3, count.intValue());
    }

    @Test
    public void reportSinkTest21() {
        LOGGER.info("---------------------------------------------------------------------------------------------");
        LOGGER.info("ReportSink TestCase 21 - Generate reports for all chart types with series and category given.");
        LOGGER.info("---------------------------------------------------------------------------------------------");

        String testReportURI = "TestReportURI/";
        String[] testChartTypes = new String[]{"Pie", "Bar", "Line"};
        AtomicInteger count = new AtomicInteger();

        Arrays.stream(testChartTypes).forEach(chartType -> {
            String testReportName = "Test" + chartType + "ChartReport";
            String testSeries = "volume";
            String testCategory = "symbol";

            String streams = "" +
                    "@App:name('TestSiddhiApp')" +
                    "define stream FooStream(symbol string, price float, volume long); " +
                    "@sink(type='report',outputpath='" + testReportURI + testReportName + "'," +
                    "chart='" + chartType + "',series='" + testSeries + "',category='" + testCategory + "'," +
                    "@map(type='json')) " +
                    "define stream BarStream (symbol string,price float, volume long); ";

            String query = "" +
                    "from FooStream " +
                    "select * " +
                    "insert into BarStream; ";

            SiddhiManager siddhiManager = new SiddhiManager();

            SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);
            InputHandler stockStream = siddhiAppRuntime.getInputHandler("FooStream");

            siddhiAppRuntime.start();

            Event testEvent1 = new Event();
            testEvent1.setData(new Object[]{"WSO2", 55.6f, 100L});

            Event testEvent2 = new Event();
            testEvent2.setData(new Object[]{"IBM", 57.678f, 100L});

            Event testEvent3 = new Event();
            testEvent3.setData(new Object[]{"GOOGLE", 50f, 100L});

            try {
                stockStream.send(new Event[]{testEvent1, testEvent2, testEvent3});
            } catch (InterruptedException e) {
                LOGGER.error(e);
            }

            File sink = new File(testReportURI + testReportName + ".pdf");
            if (sink.exists()) {
                count.incrementAndGet();
            }
            siddhiAppRuntime.shutdown();
        });
        AssertJUnit.assertEquals(3, count.intValue());
    }

    @Test
    public void reportSinkTest22() throws InterruptedException {
        LOGGER.info("------------------------------------------------------------------------------");
        LOGGER.info("ReportSink TestCase 22 - Generate reports with header and footer images given.");
        LOGGER.info("------------------------------------------------------------------------------");

        String testReportName = "TestHeaderFooterReport";
        String testReportURI = "TestReportURI/";
        String imagePath = classLoader.getResource("stream-processor.png").getPath();

        String streams = "" +
                "@App:name('TestSiddhiApp')" +
                "define stream FooStream(symbol string, price float, volume long); " +
                "@sink(type='report',outputpath='" + testReportURI + testReportName + "'," +
                "header='" + imagePath + "',footer='" + imagePath + "',@map(type='json')) " +
                "define stream BarStream (symbol string,price float, volume long); ";

        String query = "" +
                "from FooStream " +
                "select * " +
                "insert into BarStream; ";

        SiddhiManager siddhiManager = new SiddhiManager();
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);
        InputHandler stockStream = siddhiAppRuntime.getInputHandler("FooStream");

        siddhiAppRuntime.start();

        Event testEvent1 = new Event();
        testEvent1.setData(new Object[]{"WSO2", 55.6f, 100L});

        Event testEvent2 = new Event();
        testEvent2.setData(new Object[]{"IBM", 57.678f, 100L});

        Event testEvent3 = new Event();
        testEvent3.setData(new Object[]{"GOOGLE", 50f, 100L});

        Event testEvent4 = new Event();
        testEvent4.setData(new Object[]{"WSO2", 55.6f, 100L});

        stockStream.send(new Event[]{testEvent1, testEvent2, testEvent3, testEvent4});

        File sink = new File(testReportURI + testReportName + ".pdf");
        AssertJUnit.assertTrue(sink.exists());
        siddhiAppRuntime.shutdown();
    }

    @Test
    public void reportSinkTest23() throws InterruptedException {
        LOGGER.info("------------------------------------------------------------------");
        LOGGER.info("ReportSink TestCase 23 - Generate reports with empty header given.");
        LOGGER.info("------------------------------------------------------------------");

        String testReportName = "TestEmptyHeaderReport";
        String testReportURI = "TestReportURI/";
        String imagePath = "";

        String streams = "" +
                "@App:name('TestSiddhiApp')" +
                "define stream FooStream(symbol string, price float, volume long); " +
                "@sink(type='report',outputpath='" + testReportURI + testReportName + "'," +
                "header='" + imagePath + "',@map(type='json')) " +
                "define stream BarStream (symbol string,price float, volume long); ";

        String query = "" +
                "from FooStream " +
                "select * " +
                "insert into BarStream; ";

        SiddhiManager siddhiManager = new SiddhiManager();
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);
        InputHandler stockStream = siddhiAppRuntime.getInputHandler("FooStream");

        siddhiAppRuntime.start();

        Event testEvent1 = new Event();
        testEvent1.setData(new Object[]{"WSO2", 55.6f, 100L});

        Event testEvent2 = new Event();
        testEvent2.setData(new Object[]{"IBM", 57.678f, 100L});

        Event testEvent3 = new Event();
        testEvent3.setData(new Object[]{"GOOGLE", 50f, 100L});

        Event testEvent4 = new Event();
        testEvent4.setData(new Object[]{"WSO2", 55.6f, 100L});

        stockStream.send(new Event[]{testEvent1, testEvent2, testEvent3, testEvent4});

        File sink = new File(testReportURI + testReportName + ".pdf");
        AssertJUnit.assertTrue(sink.exists());
        siddhiAppRuntime.shutdown();
    }

    @Test
    public void reportSinkTest24() throws InterruptedException {
        LOGGER.info("----------------------------------------------------------------------");
        LOGGER.info("ReportSink TestCase 24 - Generate reports with invalid map type given.");
        LOGGER.info("----------------------------------------------------------------------");

        String testReportName = "TestInvalidMapTypeReport";
        String testReportURI = "TestReportURI/";
        String imagePath = "";
        String testMapType = "xml";

        String streams = "" +
                "@App:name('TestSiddhiApp')" +
                "define stream FooStream(symbol string, price float, volume long); " +
                "@sink(type='report',outputpath='" + testReportURI + testReportName + "'," +
                "header='" + imagePath + "',@map(type='" + testMapType + "')) " +
                "define stream BarStream (symbol string,price float, volume long); ";

        String query = "" +
                "from FooStream " +
                "select * " +
                "insert into BarStream; ";

        Event testEvent1 = new Event();
        testEvent1.setData(new Object[]{"WSO2", 55.6f, 100L});

        Event testEvent2 = new Event();
        testEvent2.setData(new Object[]{"IBM", 57.678f, 100L});

        Event testEvent3 = new Event();
        testEvent3.setData(new Object[]{"GOOGLE", 50f, 100L});

        Event testEvent4 = new Event();
        testEvent4.setData(new Object[]{"WSO2", 55.6f, 100L});

        try {
            SiddhiManager siddhiManager = new SiddhiManager();
            SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);
            InputHandler stockStream = siddhiAppRuntime.getInputHandler("FooStream");
            siddhiAppRuntime.start();
            stockStream.send(new Event[]{testEvent1, testEvent2, testEvent3, testEvent4});
            siddhiAppRuntime.shutdown();
        } catch (SiddhiAppCreationException e) {
            AssertJUnit.assertEquals("In 'report' sink of siddhi app TestSiddhiApp Invalid map type " + testMapType +
                    " Only JSON map type is allowed.", e.getMessageWithOutContext());
        }
    }

    @Test
    public void reportSinkTest25() throws InterruptedException {
        LOGGER.info("-----------------------------------------------------------------------");
        LOGGER.info("ReportSink TestCase 25 - Generate reports with invalid parameter given.");
        LOGGER.info("-----------------------------------------------------------------------");

        String testReportName = "invalidParameter";
        String testReportURI = "TestReportURI/";

        String streams = "" +
                "@App:name('TestSiddhiApp')" +
                "define stream FooStream(symbol string, price float, volume long); " +
                "@sink(type='report',outputpath='" + testReportURI + "{" + testReportName + "}" + "'," +
                "@map(type='json')) " +
                "define stream BarStream (symbol string,price float, volume long); ";

        String query = "" +
                "from FooStream " +
                "select * " +
                "insert into BarStream; ";

        Event testEvent1 = new Event();
        testEvent1.setData(new Object[]{"WSO2", 55.6f, 100L});

        Event testEvent2 = new Event();
        testEvent2.setData(new Object[]{"IBM", 57.678f, 100L});

        Event testEvent3 = new Event();
        testEvent3.setData(new Object[]{"GOOGLE", 50f, 100L});

        Event testEvent4 = new Event();
        testEvent4.setData(new Object[]{"WSO2", 55.6f, 100L});

        try {
            SiddhiManager siddhiManager = new SiddhiManager();
            SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);
            InputHandler stockStream = siddhiAppRuntime.getInputHandler("FooStream");
            siddhiAppRuntime.start();
            stockStream.send(new Event[]{testEvent1, testEvent2, testEvent3, testEvent4});
            siddhiAppRuntime.shutdown();
        } catch (SiddhiAppCreationException e) {
            AssertJUnit.assertEquals("In 'report' sink of siddhi app TestSiddhiApp Invalid Property '" +
                    testReportName + "'. No such parameter in the stream definition", e.getMessageWithOutContext());
        }
    }

    @Test
    public void reportSinkTest26() throws InterruptedException {
        LOGGER.info("-----------------------------------------------------------------------------------------");
        LOGGER.info("ReportSink TestCase 26 - Generate reports with parameter given for chart series/category.");
        LOGGER.info("-----------------------------------------------------------------------------------------");

        String testReportName = "TestInvalidSeriesReport";
        String testReportURI = "TestReportURI/";
        String testSeriesName = "invalidSeriesPara";

        String streams = "" +
                "@App:name('TestSiddhiApp')" +
                "define stream FooStream(symbol string, price float, volume long); " +
                "@sink(type='report',outputpath='" + testReportURI + testReportName + "'," +
                "chart='line', series='" + testSeriesName + "',@map(type='json')) " +
                "define stream BarStream (symbol string,price float, volume long); ";

        String query = "" +
                "from FooStream " +
                "select * " +
                "insert into BarStream; ";

        Event testEvent1 = new Event();
        testEvent1.setData(new Object[]{"WSO2", 55.6f, 100L});

        Event testEvent2 = new Event();
        testEvent2.setData(new Object[]{"IBM", 57.678f, 100L});

        Event testEvent3 = new Event();
        testEvent3.setData(new Object[]{"GOOGLE", 50f, 100L});

        Event testEvent4 = new Event();
        testEvent4.setData(new Object[]{"WSO2", 55.6f, 100L});

        try {
            SiddhiManager siddhiManager = new SiddhiManager();
            SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);
            InputHandler stockStream = siddhiAppRuntime.getInputHandler("FooStream");
            siddhiAppRuntime.start();
            stockStream.send(new Event[]{testEvent1, testEvent2, testEvent3, testEvent4});
            siddhiAppRuntime.shutdown();
        } catch (SiddhiAppCreationException e) {
            LOGGER.info(e.getMessage());
            AssertJUnit.assertEquals("In 'report' sink of siddhi app TestSiddhiApp Invalid property " +
                    testSeriesName + " for series", e.getMessageWithOutContext());
        }
    }

    @Test
    public void reportSinkTest27() throws InterruptedException {
        LOGGER.info("-----------------------------------------------------------------------------------------");
        LOGGER.info("ReportSink TestCase 27 - Generate reports with invalid dynamic parameter for report name.");
        LOGGER.info("-----------------------------------------------------------------------------------------");

        String testReportName = "invalidParameter";
        String testReportURI = "TestReportURI/";

        String streams = "" +
                "@App:name('TestSiddhiApp')" +
                "define stream FooStream(symbol string, price float, volume long); " +
                "@sink(type='report',outputpath='" + testReportURI + "{" + testReportName + "}'," +
                "@map(type='json')) " +
                "define stream BarStream (symbol string,price float, volume long); ";

        String query = "" +
                "from FooStream " +
                "select * " +
                "insert into BarStream; ";

        Event testEvent1 = new Event();
        testEvent1.setData(new Object[]{"WSO2", 55.6f, 100L});

        Event testEvent2 = new Event();
        testEvent2.setData(new Object[]{"IBM", 57.678f, 100L});

        Event testEvent3 = new Event();
        testEvent3.setData(new Object[]{"GOOGLE", 50f, 100L});

        Event testEvent4 = new Event();
        testEvent4.setData(new Object[]{"WSO2", 55.6f, 100L});

        try {
            SiddhiManager siddhiManager = new SiddhiManager();
            SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);
            InputHandler stockStream = siddhiAppRuntime.getInputHandler("FooStream");
            siddhiAppRuntime.start();
            stockStream.send(new Event[]{testEvent1, testEvent2, testEvent3, testEvent4});
            siddhiAppRuntime.shutdown();
        } catch (SiddhiAppCreationException e) {
            AssertJUnit.assertEquals("In 'report' sink of siddhi app TestSiddhiApp Invalid Property '" +
                    testReportName + "'. No such parameter in the stream definition", e.getMessageWithOutContext());
        }
    }

    @Test
    public void reportSinkTest28() throws InterruptedException {
        LOGGER.info("------------------------------------------------------------------------");
        LOGGER.info("ReportSink TestCase 28 - Generate reports with invalid jrxml file given.");
        LOGGER.info("------------------------------------------------------------------------");

        String testReportName = "TestReport";
        String testReportURI = "TestReportURI/";
        String testTemplateURI = classLoader.getResource("invalidTemplate.jrxm").getFile();

        String streams = "" +
                "@App:name('TestSiddhiApp')" +
                "define stream FooStream(symbol string, price float, volume long); " +
                "@sink(type='report',outputpath='" + testReportURI + testReportName + "'," +
                "template='" + testTemplateURI + "',@map(type='json')) " +
                "define stream BarStream (symbol string,price float, volume long); ";

        String query = "" +
                "from FooStream " +
                "select * " +
                "insert into BarStream; ";

        Event testEvent1 = new Event();
        testEvent1.setData(new Object[]{"WSO2", 55.6f, 100L});

        Event testEvent2 = new Event();
        testEvent2.setData(new Object[]{"IBM", 57.678f, 100L});

        Event testEvent3 = new Event();
        testEvent3.setData(new Object[]{"GOOGLE", 50f, 100L});

        Event testEvent4 = new Event();
        testEvent4.setData(new Object[]{"WSO2", 55.6f, 100L});

        try {
            SiddhiManager siddhiManager = new SiddhiManager();
            SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);
            InputHandler stockStream = siddhiAppRuntime.getInputHandler("FooStream");
            siddhiAppRuntime.start();
            stockStream.send(new Event[]{testEvent1, testEvent2, testEvent3, testEvent4});
            siddhiAppRuntime.shutdown();
        } catch (SiddhiAppCreationException e) {
            AssertJUnit.assertEquals("In 'report' sink of siddhi app TestSiddhiApp " + testTemplateURI + " is invalid" +
                    "." + ReportConstants.TEMPLATE + " should have a JRXML template", e.getMessageWithOutContext());
        }
    }

    @Test
    public void reportSinkTest29() throws InterruptedException {
        LOGGER.info("-------------------------------------------------------------------------");
        LOGGER.info("ReportSink TestCase 29 - Generate reports with invalid header image given.");
        LOGGER.info("-------------------------------------------------------------------------");

        String testReportName = "TestReport";
        String testReportURI = "TestReportURI/";
        String testImageURI = classLoader.getResource("stream-processor.pn").getFile();

        String streams = "" +
                "@App:name('TestSiddhiApp')" +
                "define stream FooStream(symbol string, price float, volume long); " +
                "@sink(type='report',outputpath='" + testReportURI + testReportName + "'," +
                "header='" + testImageURI + "',@map(type='json')) " +
                "define stream BarStream (symbol string,price float, volume long); ";

        String query = "" +
                "from FooStream " +
                "select * " +
                "insert into BarStream; ";

        Event testEvent1 = new Event();
        testEvent1.setData(new Object[]{"WSO2", 55.6f, 100L});

        Event testEvent2 = new Event();
        testEvent2.setData(new Object[]{"IBM", 57.678f, 100L});

        Event testEvent3 = new Event();
        testEvent3.setData(new Object[]{"GOOGLE", 50f, 100L});

        Event testEvent4 = new Event();
        testEvent4.setData(new Object[]{"WSO2", 55.6f, 100L});

        try {
            SiddhiManager siddhiManager = new SiddhiManager();
            SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);
            InputHandler stockStream = siddhiAppRuntime.getInputHandler("FooStream");
            siddhiAppRuntime.start();
            stockStream.send(new Event[]{testEvent1, testEvent2, testEvent3, testEvent4});
            siddhiAppRuntime.shutdown();
        } catch (SiddhiAppCreationException e) {
            AssertJUnit.assertEquals("In 'report' sink of siddhi app TestSiddhiApp Invalid path " + testImageURI + "." +
                    " " + ReportConstants.HEADER + " should be an image", e.getMessageWithOutContext());
        }
    }

    @Test
    public void reportSinkTest30() throws InterruptedException {
        LOGGER.info("---------------------------------------------------------------------------");
        LOGGER.info("ReportSink TestCase 30 - No numeric stream attribute for line chart series.");
        LOGGER.info("---------------------------------------------------------------------------");

        String testReportName = "TestReport";
        String testReportURI = "TestReportURI/";

        String streams = "" +
                "@App:name('TestSiddhiApp')" +
                "define stream FooStream(symbol string, price string, volume string); " +
                "@sink(type='report',outputpath='" + testReportURI + testReportName + "'," +
                "chart='line',@map(type='json')) " +
                "define stream BarStream (symbol string,price string, volume string); ";

        String query = "" +
                "from FooStream " +
                "select * " +
                "insert into BarStream; ";

        Event testEvent1 = new Event();
        testEvent1.setData(new Object[]{"WSO2", "55.6f", "100L"});

        Event testEvent2 = new Event();
        testEvent2.setData(new Object[]{"IBM", "57.678f", "100L"});

        Event testEvent3 = new Event();
        testEvent3.setData(new Object[]{"GOOGLE", "50f", "100L"});

        Event testEvent4 = new Event();
        testEvent4.setData(new Object[]{"WSO2", "55.6f", "100L"});

        try {
            SiddhiManager siddhiManager = new SiddhiManager();
            SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);
            InputHandler stockStream = siddhiAppRuntime.getInputHandler("FooStream");
            siddhiAppRuntime.start();
            stockStream.send(new Event[]{testEvent1, testEvent2, testEvent3, testEvent4});
            siddhiAppRuntime.shutdown();
        } catch (SiddhiAppCreationException e) {
            AssertJUnit.assertEquals("In 'report' sink of siddhi app TestSiddhiApp line chart definition is invalid. " +
                            "There is no numeric stream attribute for the series in. Provide a numeric series column.",
                    e.getMessageWithOutContext());
        }
    }

    @Test
    public void reportSinkTest31() throws InterruptedException {
        LOGGER.info("---------------------------------------------------------------------------");
        LOGGER.info("ReportSink TestCase 31 - Configure siddhi to generate reports in csv format");
        LOGGER.info("---------------------------------------------------------------------------");

        String reportName = "testReport";
        String streams = "" +
                "@App:name('TestSiddhiApp')" +
                "define stream FooStream(symbol string, price int, volume long, testval bool); " +
                "@sink(type='report',outputpath='" + reportName + "',output.format='csv',@map(type='json')) " +
                "define stream BarStream (symbol string,price int, volume long, testval bool); ";

        String query = "" +
                "from FooStream " +
                "select * " +
                "insert into BarStream; ";

        SiddhiManager siddhiManager = new SiddhiManager();
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);
        InputHandler stockStream = siddhiAppRuntime.getInputHandler("FooStream");

        siddhiAppRuntime.start();

        Event testEvent1 = new Event();
        testEvent1.setData(new Object[]{"WSO2", 55.6f, 100L, true});

        Event testEvent2 = new Event();
        testEvent2.setData(new Object[]{"IBM", 57.8f, 100L, false});

        Event testEvent3 = new Event();
        testEvent3.setData(new Object[]{"GOOGLE", 50f, 100L, true});

        Event testEvent4 = new Event();
        testEvent4.setData(new Object[]{"WSO2", 55.6f, 100L, true});

        stockStream.send(new Event[]{testEvent1, testEvent2, testEvent3, testEvent4});

        File sink = new File(reportName + ".csv");
        AssertJUnit.assertTrue(sink.exists());
        siddhiAppRuntime.shutdown();
    }

    @Test
    public void reportSinkTest32() throws InterruptedException {
        LOGGER.info("----------------------------------------------------------------------------");
        LOGGER.info("ReportSink TestCase 32 - Configure siddhi to generate reports in xlsx format");
        LOGGER.info("----------------------------------------------------------------------------");

        String reportName = "testReport";
        String streams = "" +
                "@App:name('TestSiddhiApp')" +
                "define stream FooStream(symbol string, price int, volume long, testval bool); " +
                "@sink(type='report',outputpath='" + reportName + "',output.format='excel',@map(type='json')) " +
                "define stream BarStream (symbol string,price int, volume long, testval bool); ";

        String query = "" +
                "from FooStream " +
                "select * " +
                "insert into BarStream; ";

        SiddhiManager siddhiManager = new SiddhiManager();
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);
        InputHandler stockStream = siddhiAppRuntime.getInputHandler("FooStream");

        siddhiAppRuntime.start();

        Event testEvent1 = new Event();
        testEvent1.setData(new Object[]{"WSO2", 55.6f, 100L, true});

        Event testEvent2 = new Event();
        testEvent2.setData(new Object[]{"IBM", 57.8f, 100L, false});

        Event testEvent3 = new Event();
        testEvent3.setData(new Object[]{"GOOGLE", 50f, 100L, true});

        Event testEvent4 = new Event();
        testEvent4.setData(new Object[]{"WSO2", 55.6f, 100L, true});

        stockStream.send(new Event[]{testEvent1, testEvent2, testEvent3, testEvent4});

        File sink = new File(reportName + ".xlsx");
        AssertJUnit.assertTrue(sink.exists());
        siddhiAppRuntime.shutdown();
    }
}
