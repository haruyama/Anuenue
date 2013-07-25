/*
 * Copyright (c) The Anuenue Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.unigram.anuenue.client.solr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Class which updates data.
 */
public final class SolrClient {

    /** Solr ping path. */
    private static final String SOLR_PING_PATH = "/solr/admin/ping";

    /** Solr update path. */
    private static final String SOLR_UPDATE_PATH = "/solr/update";

    /** Solr updateCSV path and parameter for TSV. */
    private static final String SOLR_UPDATE_TSV_PATH_AND_QUERY = "/solr/update?separator=%09&encapsulator=%09";

    /** Solr update/csv path for TSV. */
    private static final String SOLR_UPDATE_TSV_VIA_STREAM_FILE_PATH_AND_QUERY
        = "/solr/update/csv?separator=%09&encapsulator=%09&stream.contentType=text/plain;charset=utf-8&stream.file=";

    /** port number of Solr instance. */
    private final int portNumber;

    /** host name of Solr instance. */
    private final String hostName;

    /** logger. */
    private static final Log LOG = LogFactory.getLog(SolrClient.class);

    /** encoding for HTTP. */
    private static final String HTTP_ENCODING = "UTF-8";

    /** property: anuenue.http.connect.timeout. */
    private static final String PROPERTY_HTTP_CONNECT_TIMEOUT = "anuenue.http.connect.timeout";

    /** HTTP read timeout(msec). */
    private static final String DEFAULT_HTTP_CONNECT_TIMEOUT = "60000";

    /** HTTP read timeout(msec). */
    private final int connectTimeout;

    /** property: anuenue.http.read.timeout. */
    private static final String PROPERTY_HTTP_READ_TIMEOUT = "anuenue.http.read.timeout";

    /** default value of HTTP read timeout(msec). */
    private static final String DEFAULT_HTTP_READ_TIMEOUT = "120000";

    /** HTTP read timeout(msec). */
    private int readTimeout;
    /**
     * Constructor.
     *
     * @param host
     *            host name
     * @param port
     *            port number
     */
    public SolrClient(final String host, final int port) {
        hostName = host;
        portNumber = port;

        connectTimeout = Integer.parseInt(System.getProperty(PROPERTY_HTTP_CONNECT_TIMEOUT, DEFAULT_HTTP_CONNECT_TIMEOUT));
        readTimeout = Integer.parseInt(System.getProperty(PROPERTY_HTTP_READ_TIMEOUT, DEFAULT_HTTP_READ_TIMEOUT));
    }

    /**
     * Set new read timeout.
     *
     * @param timeout new read timeout value
     */
    public void setReadTimeout(final int timeout) {
        readTimeout = timeout;
    }

    /**
     * Update a TSV file.
     *
     * @param file File
     *
     * @return the result of post.
     * @throws IOException in case of IO error
     */
    public String updateTSVFile(final File file) throws IOException {

        try (FileInputStream fis = new FileInputStream(file);
                InputStreamReader isr = new InputStreamReader(fis, HTTP_ENCODING);
                Reader reader = new BufferedReader(isr)) {
            return updateTSVData(reader);
        }
    }

    /**
     * update String TSV data.
     *
     * @param data String data.
     *
     * @return the result of post
     * @throws IOException in case of IO error
     */
    public String updateTSVData(final String data) throws IOException {
        try (Reader reader = new StringReader(data)) {
            return updateTSVData(reader);
        }
    }

    /**
     * Update TSV data.
     *
     * @param reader Reader
     *
     * @return the result of post
     * @throws IOException in case of IO error
     */
    private String updateTSVData(final Reader reader) throws IOException {
        URL solrURL = new URL("http", hostName, portNumber, SOLR_UPDATE_TSV_PATH_AND_QUERY);
        String contentType = "text/csv; charset=" + HTTP_ENCODING;
        return post(reader, solrURL, contentType);
    }

    /**
     * Update TSV file via stream.file parameter.
     * @param argument filename
     *
     * @return the result of get
     * @throws IOException in case of IO error
     */
    public String updateTSVViaStreamFile(final String argument) throws IOException  {
        return get(new URL("http", hostName, portNumber, SOLR_UPDATE_TSV_VIA_STREAM_FILE_PATH_AND_QUERY
                + URLEncoder.encode(new File(argument).getAbsolutePath(), "UTF-8")));
    }

    /**
     * Ping to a Solr instance.
     *
     * @return the result of ping
     * @throws IOException in case of IO error
     */
    public String ping() throws IOException {
        return get(new URL("http", hostName, portNumber, SOLR_PING_PATH));
    }

    /**
     * Update XML.
     *
     * @param data String data
     *
     * @return the result of post
     * @throws IOException in case of IO error
     */
    public String updateXML(final String data) throws IOException {

        try (Reader reader = new StringReader(data)) {
            return post(reader);
        }
    }

    /**
     * Post data.
     *
     * @param reader Reader
     *
     * @return the result of post
     * @throws IOException in case of IO error
     */
    private String post(final Reader reader) throws IOException {
        URL solrURL = new URL("http", hostName, portNumber, SOLR_UPDATE_PATH);
        String contentType = "text/xml; charset=" + HTTP_ENCODING;
        return post(reader, solrURL, contentType);
    }

    /**
     * Post data.
     *
     * @param reader data
     * @param solrURL the URL for updating solr data
     * @param contentType content-type
     *
     * @return the result of post
     * @throws IOException in case of IO error
     */
    private String post(final Reader reader, final URL solrURL,
            final String contentType) throws IOException {
        HttpURLConnection urlc = null;
        urlc = getPostHttpURLConnection(solrURL, contentType);
        return post(reader, urlc);
    }

    /**
     * Post data.
     *
     * @param reader Reader
     * @param urlc HttpURLConnection
     *
     * @return the result of post
     * @throws IOException in case of IO error
     */
    public static String post(final Reader reader, final HttpURLConnection urlc)
            throws IOException {
        submitData(reader, urlc);
        return getResult(urlc);
    }


    /**
     * Get data.
     *
     * @param solrURL the URL for updating solr data
     *
     * @return the result of get
     * @throws IOException in case of IO error
     */
    private String get(final URL solrURL) throws IOException {
        HttpURLConnection urlc = getGetHttpURLConnection(solrURL);
        return getResult(urlc);
    }

    /**
     * Get data and copy them to HttpServletResponse.
     *
     * @param path     the URL for updating solr data
     * @param response HttpServletResponse
     *
     * @throws IOException in case of IO error
     */
    public void getAndCopyToHttpServletResponse(final String path, final HttpServletResponse response) throws IOException {
        HttpURLConnection urlc = getGetHttpURLConnection(new URL("http", hostName, portNumber, path));

        response.setContentType(urlc.getContentType());
        try (Writer writer = response.getWriter();
                InputStream inputStream = urlc.getInputStream()) {
            IOUtils.copy(inputStream, writer, HTTP_ENCODING);
        }
    }
    /**
     * Submit data.
     *
     * @param reader Reader
     * @param urlc HttpURLConnection
     * @throws IOException in case of IO error
     */
    private static void submitData(final Reader reader, final HttpURLConnection urlc)
            throws IOException {
        try (OutputStream outputStream = urlc.getOutputStream()) {
            IOUtils.copy(reader, outputStream, HTTP_ENCODING);
        }
    }

    /**
     * Get result from HttpURLConnection.
     *
     * @param urlc HttpURLConnection
     *
     * @return result from given HttpURLConnection
     * @throws IOException in case of IO error
     */
    private static String getResult(final HttpURLConnection urlc) throws IOException {

        try (StringWriter writer = new StringWriter();
                InputStream inputStream = urlc.getInputStream()) {
            IOUtils.copy(inputStream, writer, HTTP_ENCODING);
            return writer.toString();
        }
    }

    /**
     * Get HttpURLConnection.
     *
     * @param solrURL URL for updating sorl data
     * @param contentType content-type
     * @param method HTTP method
     * @return HttpURLConnection
     */
    private HttpURLConnection getHttpURLConnection(final URL solrURL,
            final String contentType, final String method) {
        HttpURLConnection urlc = null;

        try {
            urlc = (HttpURLConnection) solrURL.openConnection();
            urlc.setRequestMethod(method);
            urlc.setDoOutput(true);
            urlc.setDoInput(true);
            urlc.setUseCaches(false);
            urlc.setAllowUserInteraction(false);
            if (contentType != null) {
                urlc.setRequestProperty("Content-type", contentType);
            }
            urlc.setConnectTimeout(connectTimeout);
            urlc.setReadTimeout(readTimeout);
        } catch (ProtocolException e) {
            throw new RuntimeException(
                    "Shouldn't happen: HttpURLConnection doesn't support POST?"
                            + e.toString(), e);
        } catch (IOException e) {
            LOG.error(e.toString());
            throw new RuntimeException(e);
        }
        return urlc;
    }

    /**
     * Get HttpURLConnection via POST.
     *
     * @param solrURL URL for updating sorl data
     * @param contentType content-type
     * @return HttpURLConnection
     */
    private HttpURLConnection getPostHttpURLConnection(final URL solrURL,
            final String contentType) {
        return getHttpURLConnection(solrURL, contentType, "POST");
    }

    /**
     * Get HttpURLConnection via GET.
     *
     * @param solrURL URL for updating sorl data
     * @return HttpURLConnection
     */
    private HttpURLConnection getGetHttpURLConnection(final URL solrURL) {
        return getHttpURLConnection(solrURL, null, "GET");
    }

}
