package pl.exsio.ck.main.app;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author exsio
 */
public class App {

    private static ApplicationContext ctx;

    public static void main(String[] args) {

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                App.ctx = new ClassPathXmlApplicationContext("context.xml");
            }
        });
    }

    public static ApplicationContext getContext() {
        return App.ctx;
    }

}
