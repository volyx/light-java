//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnio.Options;

import java.io.IOException;

public class Server {
    static final Logger logger = LoggerFactory.getLogger(Server.class);
    static Undertow server = null;

    public Server() {
    }

    public static void main(String[] args) {
        logger.info("server starts");
        start();
    }

    public static void start() {
        addDaemonShutdownHook();

        TemplateLoader loader = new ClassPathTemplateLoader();
        loader.setPrefix("/templates");
        loader.setSuffix(".html");
        Handlebars handlebars = new Handlebars(loader);

        final Template template;
        try {
            template = handlebars.compile("index");
        } catch (IOException e) {
            throw new RuntimeException();
        }

        System.out.println();

        Object handler = Handlers.path()
                .addPrefixPath("/", new HttpHandler() {
                            public void handleRequest(HttpServerExchange exchange) {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("name", "Hello World!");
                                try {
                                    exchange.getResponseSender().send(template.apply("Handlebars.java"));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                );

        if (handler == null) {
            logger.warn("No route handler provider available in the classpath");
        } else {

            server = Undertow.builder()
                    .addHttpListener(8081, "0.0.0.0")
                    .setBufferSize(16384)
                    .setIoThreads(Runtime.getRuntime().availableProcessors() * 2)
                    .setSocketOption(Options.BACKLOG, 10000)
                    .setServerOption(UndertowOptions.ALWAYS_SET_KEEP_ALIVE, Boolean.FALSE)
                    .setServerOption(UndertowOptions.ALWAYS_SET_DATE, Boolean.TRUE)
                    .setServerOption(UndertowOptions.RECORD_REQUEST_START_TIME, Boolean.FALSE)
                    .setHandler(Handlers.header((HttpHandler) handler, "Server", "Undertow")).setWorkerThreads(200).build();
            server.start();
        }
    }

    public static void stop() {
        if (server != null) {
            server.stop();
        }

    }

    public static void shutdown() {
        stop();
        logger.info("Cleaning up before server shutdown");
    }

    protected static void addDaemonShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                Server.shutdown();
            }
        });
    }
}
