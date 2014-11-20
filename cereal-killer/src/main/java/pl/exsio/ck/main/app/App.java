package pl.exsio.ck.main.app;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import pl.exsio.ck.main.view.AbstractMainFrame;

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
                ctx = new ClassPathXmlApplicationContext("context.xml");
                AbstractMainFrame main = (AbstractMainFrame) ctx.getBean("mainFrame");
                main.setVisible(true);
            }
        });
    }

    public static ApplicationContext getContext() {
        return ctx;
    }

}
