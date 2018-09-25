import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.config.Lookup;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.impl.auth.BasicSchemeFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.ProxyAuthenticationStrategy;
import org.asynchttpclient.*;
import org.asynchttpclient.proxy.ProxyServer;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class Main {
    private static final String inputFileName = "YOURINPUTFILE.txt";
    private static final String proxyUsername = "";
    private static final String proxyPassword = "";
    private static final int proxyPort = 0;
    private static final String ipToolURL = "http://checkip.instantproxies.com/";

    private static final String[] proxyHostList = new String [] {
            "192.225.110.120",
            "104.160.232.87",
            "104.160.239.76",
            "173.246.164.16",
            "173.246.165.222",
            "173.246.166.49",
            "173.246.167.222",
            "107.174.18.222",
            "107.175.96.204",
            "107.173.75.59",
            "107.173.59.15",
            "107.175.42.182",
            "185.191.231.176",
            "185.191.229.29",
            "185.199.224.69",
            "172.86.120.234",
            "104.200.254.62",
            "155.254.181.106",
            "104.200.255.42",
            "155.254.180.64",
            "198.41.105.29",
            "107.182.5.135",
            "107.182.6.223",
            "167.88.10.139",
            "172.93.237.85",
    };


    public static void main(String[] args) throws Exception {
        Logger root = (Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);
        yaqt();

        /*
        try (BufferedReader br = new BufferedReader(new FileReader(inputFileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                ArrayList<String> companyNameSuggestions = query(line);
                writeSuggestionsForCompanyToFile(companyNameSuggestions);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } */
    }

    private static ArrayList<String> query(final String companyName) throws IOException {
        ArrayList<String> companyNameSuggestions = new ArrayList();
        int rnd = (int) (Math.random()*3);


        AsyncHttpClientConfig proxiedCF = new DefaultAsyncHttpClientConfig.Builder().setUserAgent(pickUserAgent()).build();
        AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient(proxiedCF);
        Future<Response> f = asyncHttpClient.prepareGet("http://google.com/complete/search?q=" + companyName + "&output=toolbar&gl=de&hl=de").setProxyServer(new ProxyServer.Builder(pickProxyServer(), proxyPort))
                .setRealm(new Realm.Builder(proxyUsername, proxyPassword)).execute();

        Response r = null;
        try {r = f.get();}
        catch (InterruptedException | ExecutionException e) {e.printStackTrace();}

        Pattern p = Pattern.compile("\"([^\"]*)\"");
        Matcher m = p.matcher(r.toString());
        while (m.find()) {
            if (!m.group(1).equals("1.0") && !m.group(1).contains("P3P")) {
                System.out.println(m.group(1));
                companyNameSuggestions.add(m.group(1));
            }
        }

        asyncHttpClient.close();
        return companyNameSuggestions;
    }

    private static void writeSuggestionsForCompanyToFile(final ArrayList<String> writeThis){
        try(FileWriter fw = new FileWriter("output_of_" + inputFileName, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            if (!writeThis.isEmpty()) {
                out.println(writeThis);
            }
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String pickUserAgent() {
        return "burning_dandelion";
    }

    private static String getProxyHost(final int randomNumber) {
        return proxyHostList[randomNumber];
    }

    private static int getProxyPort(final int randomNumber) {
        return proxyPort;
    }

    private static String pickProxyServer() {
        int r = (int) (Math.random()*1);
        String name = new String [] {"193.37.152.6",
                "31.3.145.189",
                "46.4.123.185",
                "93.192.41.216",
                "134.119.223.242",
                "52.57.95.123",
                "138.201.240.238",
                "212.37.49.44",
                "212.184.12.11",
                "62.159.193.83"}[r];

        //System.out.println("Using Proxy IP:"+ name);
        return "172.93.237.85";
    }

    private static boolean verifyProxyIntegrity() throws IOException {

        System.setProperty("http.proxyHost", proxyHostList[0]);
        System.setProperty("http.proxyPort", Integer.toString(proxyPort));
        System.setProperty("http.proxyUser", proxyUsername);
        System.setProperty("http.proxyPassword", proxyPassword);

        AsyncHttpClientConfig config = new DefaultAsyncHttpClientConfig.Builder().build();
        final AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient(config);

        Future<Response> f = asyncHttpClient.prepareGet("http://checkip.instantproxies.com/").execute();

        Response r = null;
        try {
            r = f.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        System.out.println(r.toString());
        asyncHttpClient.close();
        return false;

        /* test that proxy IP is used
        Future<Response> g = asyncHttpClient.prepareGet("http://checkip.instantproxies.com/").setProxyServer(new ProxyServer.Builder(getProxyHost(rnd), getProxyPort(rnd))).execute();
        Response s = null;
        try {
            s = g.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        System.out.println(s.toString());
        */
    }

    private static void newQueryTest() throws UnirestException {

        HttpClientBuilder clientBuilder = HttpClientBuilder.create();
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(proxyUsername, proxyPassword));
        clientBuilder.useSystemProperties();
        clientBuilder.setProxy(new HttpHost("172.93.237.85", 60099)); // TODO
        clientBuilder.setDefaultCredentialsProvider(credsProvider);
        clientBuilder.setProxyAuthenticationStrategy(new ProxyAuthenticationStrategy());

        Lookup<AuthSchemeProvider> authProviders = RegistryBuilder.<AuthSchemeProvider>create()
                .register(AuthSchemes.BASIC, new BasicSchemeFactory())
                .build();
        clientBuilder.setDefaultAuthSchemeRegistry(authProviders);

        Unirest.setHttpClient(clientBuilder.build());

        HttpResponse response = Unirest.get(ipToolURL).asString();
        System.out.println(response.getRawBody());
    }

    private static void yaqt() throws IOException {
        Realm realm = new Realm.Builder (proxyUsername, proxyPassword)
                .setUsePreemptiveAuth(true)
                .setScheme(Realm.AuthScheme.BASIC)
                .build();

        ProxyServer ps = new ProxyServer(pickProxyServer(), proxyPort, proxyPort, new Realm(AuthSchemes.BASIC, proxyUsername, proxyPassword));

        AsyncHttpClientConfig proxiedCF = new DefaultAsyncHttpClientConfig.Builder().setUserAgent(pickUserAgent()).build();
        AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient(proxiedCF);
        Future<Response> f = asyncHttpClient.prepareGet(ipToolURL)
                .setProxyServer(new ProxyServer.Builder(pickProxyServer(), proxyPort))
                .setRealm(realm)
                .execute();

        Response r = null;
        try {
            r = f.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        System.out.println(r.toString());
        asyncHttpClient.close();
    }
}

