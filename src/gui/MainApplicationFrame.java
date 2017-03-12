package gui;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.prefs.Preferences;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.xml.bind.SchemaOutputResolver;

import com.sun.xml.internal.fastinfoset.util.ValueArray;
import jdk.internal.org.objectweb.asm.tree.analysis.Value;
import log.Logger;
import sun.misc.IOUtils;

/**
 * Что требуется сделать:
 * 1. Метод создания меню перегружен функционалом и трудно читается. 
 * Следует разделить его на серию более простых методов (или вообще выделить отдельный класс).
 *
 */


public class MainApplicationFrame extends JFrame
{
    public LogWindow logWindow;
    public GameWindow gameWindow;
    public Window window;

    //public int logX = 10; public int logY = 10; public int logW = 300; public int logH = 800;
    public int gameX = 0; public int gameY = 0; public int gameW = 250; public int gameH = 250;
    public int winX = 50; public int winY = 50; public int winW = 350; public int winH = 1000;

    private final JDesktopPane desktopPane = new JDesktopPane();
    
    public MainApplicationFrame() throws IOException {
        //Make the big window be indented 50 pixels from each edge
        //of the screen.
        Map map = recoverWindows();//мы ничо не создали ещё, поэтому везде null
        //ток зачем вот мы в данный метод передаём окна непонятно, мы можем сюда ничего не передавать, так
        //как просто читаем из файла
        int[] logAr = (int[])map.get("logWindow");
        //System.out.println(logAr.toString());
        int[] gameAr = (int[])map.get("gameWindow");
        int[] winAr = (int[])map.get("window");

        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(winAr[2], winAr[3]);
        setSize(winAr[1], winAr[0]);
//        setBounds(inset, inset,
//            screenSize.width  - inset*2,
//            screenSize.height - inset*2);
        //setBounds(winAr[3], winAr[2], winAr[1], winAr[0]);
//        setBounds(winAr[3], winAr[2], winAr[1], winAr[0]);



        setContentPane(desktopPane);
        
        
        logWindow = createLogWindow(logAr[1], logAr[2], logAr[0], logAr[3]);
        //оно в классе меняет значение размера, надо как-то исправить, но это уже новое поле, которое с окном
        //и его размерами мало связано
        //System.out.println(logAr.toString());
        //logWindow = createLogWindow(10, 10, 300, 800);
        addWindow(logWindow);

        gameWindow = new GameWindow();
        gameWindow.setLocation(gameAr[2], gameAr[3]);
        gameWindow.setSize(gameAr[1], gameAr[0]);
        addWindow(gameWindow);

        setJMenuBar(generateLookAndFeelBars());
        setJMenuBar(generateTestBar());
        //setDefaultCloseOperation(EXIT_ON_CLOSE);//вот здесь первое задание видимо
    }
    
    protected LogWindow createLogWindow(int logX, int logY, int logW, int logH) throws IOException {

        logWindow = new LogWindow(Logger.getDefaultLogSource());//we probably shall do this here
        logWindow.setLocation(logX,logY);
        logWindow.setSize(logW, logH);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("Протокол работает");
        return logWindow;
    }
    
    protected void addWindow(JInternalFrame frame)
    {
        desktopPane.add(frame);
        frame.setVisible(true);
    }
    
//    protected JMenuBar createMenuBar() {
//        JMenuBar menuBar = new JMenuBar();
// 
//        //Set up the lone menu.
//        JMenu menu = new JMenu("Document");
//        menu.setMnemonic(KeyEvent.VK_D);
//        menuBar.add(menu);
// 
//        //Set up the first menu item.
//        JMenuItem menuItem = new JMenuItem("New");
//        menuItem.setMnemonic(KeyEvent.VK_N);
//        menuItem.setAccelerator(KeyStroke.getKeyStroke(
//                KeyEvent.VK_N, ActionEvent.ALT_MASK));
//        menuItem.setActionCommand("new");
////        menuItem.addActionListener(this);
//        menu.add(menuItem);
// 
//        //Set up the second menu item.
//        menuItem = new JMenuItem("Quit");
//        menuItem.setMnemonic(KeyEvent.VK_Q);
//        menuItem.setAccelerator(KeyStroke.getKeyStroke(
//                KeyEvent.VK_Q, ActionEvent.ALT_MASK));
//        menuItem.setActionCommand("quit");
////        menuItem.addActionListener(this);
//        menu.add(menuItem);
// 
//        return menuBar;
//    }

    private JMenuBar generateLookAndFeelBars()
    {
        JMenuBar menuBar = new JMenuBar();

        JMenu lookAndFeelMenu = new JMenu("Режим отображения");
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext().setAccessibleDescription(
                "Управление режимом отображения приложения");

        JMenuItem systemLookAndFeel = new JMenuItem("Системная схема", KeyEvent.VK_S);
        lookAndFeelMenu = addingItemLookAndFeel(lookAndFeelMenu, systemLookAndFeel);

        JMenuItem crossplatformLookAndFeel = new JMenuItem("Универсальная схема", KeyEvent.VK_S);
        lookAndFeelMenu = addingItemLookAndFeel(lookAndFeelMenu, crossplatformLookAndFeel);

        menuBar.add(lookAndFeelMenu);
        exitWindow(menuBar);
        return menuBar;
    }

    private JMenu addingItemLookAndFeel(JMenu menu, JMenuItem item)//тут точно не JMenuBar?
    {
        item.addActionListener((event) -> {
            setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            this.invalidate();
        });
        menu.add(item);
        return menu;
    }


    private JMenuBar exitWindow(JMenuBar menu)
    {
        JMenuItem exitItem = new JMenuItem("Выход", KeyEvent.VK_X | KeyEvent.VK_ALT);
        exitItem.addActionListener((event) -> {
            Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(
                    new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        });
        menu.add(exitItem);
        return menu;
    }

    private Map<String, int[]> getWindowLayout(LogWindow w1, GameWindow w2, Window w3){
        Map<String, int[]> map = new HashMap<String, int[]>();

        int windowH = w1.getHeight();
        int windowW = w1.getWidth();
        int windowX = w1.getX();
        int windowY = w1.getY();
        int[] array = { windowH, windowW, windowX, windowY };
        map.put("logWindow", array);

        int windowH2 = w2.getHeight();
        int windowW2 = w2.getWidth();
        int windowX2 = w2.getX();
        int windowY2 = w2.getY();
        int[] array2 = { windowH2, windowW2, windowX2, windowY2 };
        map.put("gameWindow", array2);

        int windowH3 = w3.getHeight();
        int windowW3 = w3.getWidth();
        int windowX3 = w3.getX();
        int windowY3 = w3.getY();
        int[] array3 = { windowH3, windowW3, windowX3, windowY3 };
        map.put("window", array3);


        return map;
    }

    private void saveWindowLayout(WindowEvent w) throws IOException//надо сделать для всех трёх окон как-то
            //скорее всего надо бы передавать в определённый метод окно, а не событие закрытия
    {
        Window window = w.getWindow();
        Map<String, int[]> map = getWindowLayout(logWindow, gameWindow, window);

       // Set<String> set = array.keySet();//по ключу тянуть значение, но у нас уже есть известный набор ключей


        BufferedWriter outputWriter = null;
        outputWriter = new BufferedWriter(new FileWriter("layout.txt"));

        int[] array = (int[])map.get("logWindow");
        outputWriter.write("logWindow: ");
        for (int i=0; i<array.length; i++)
            outputWriter.write(array[i]+" ");
        outputWriter.write(":");

        int[] array2 = (int[])map.get("gameWindow");
        outputWriter.write("gameWindow: ");
        for (int i=0; i<array2.length; i++)
            outputWriter.write(array2[i]+" ");
        outputWriter.write(":");

        int[] array3 = (int[])map.get("window");
        outputWriter.write("window: ");
        for (int i=0; i<array3.length; i++)
            outputWriter.write(array3[i]+" ");
        outputWriter.write(":");

        outputWriter.flush();
        outputWriter.close();
    }

    public void processWindowEvent(WindowEvent w) {
        int evt = w.getID();
        if (evt == WindowEvent.WINDOW_CLOSING || evt == WindowEvent.WINDOW_CLOSED) {
            // save window size and position
            try {
                saveWindowLayout(w);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.processWindowEvent(w);
    }

//    public static int[] staticMethodForLogger() throws IOException {
//        MainApplicationFrame a = new MainApplicationFrame();
//        Map map = a.recoverWindows();
//        int[] logAr = (int[])map.get("logWindow");
//        return logAr;
//
//    }

    public Map<String, int[]> recoverWindows() throws IOException {

        String s = Files.lines(Paths.get("/home/ftlka/Documents/Robots-master/layout.txt")).findFirst().get();
        //надо повытаскивать из сторки и переделать глобальные переменные
        String[] parts = s.split(":");
        System.out.println(parts[1]);

        Map map = new HashMap<String, int[]>();

        //String[] array1 = {parts[1].split("//s+")};
        System.out.println(parts[1]);

        map.put(parts[0], arrayFromString(parts[1]));
        map.put(parts[2], arrayFromString(parts[3]));
        map.put(parts[4], arrayFromString(parts[5]));
       // System.out.println(parts[1].split("//s+").toString());
        return map;
    }

    public int[] arrayFromString(String s)
    {
        String[] ar1Str = s.split("\\s+");
        String[] ar2Str = {ar1Str[1], ar1Str[2], ar1Str[3], ar1Str[4]};
        int[] intArray1 = new int[ar2Str.length];
        for(int i = 0; i < ar2Str.length; i++) {
            intArray1[i] = Integer.parseInt(ar2Str[i]);
        }

        return intArray1;
    }

    private JMenuBar generateTestBar()
    {
        JMenuBar menuBar = new JMenuBar();
        JMenu testMenu = new JMenu("Тесты");
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription(
                "Тестовые команды");

        {
            JMenuItem addLogMessageItem = new JMenuItem("Сообщение в лог", KeyEvent.VK_S);
            addLogMessageItem.addActionListener((event) -> {
                Logger.debug("Новая строка");
            });
            testMenu.add(addLogMessageItem);
        }


        menuBar.add(testMenu);
        exitWindow(menuBar);
        return menuBar;
    }


//  private JMenuBar generateMenuBar()
//    {
//        JMenuBar menuBar = new JMenuBar();
//
//        JMenu lookAndFeelMenu = new JMenu("Режим отображения");
//        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
//        lookAndFeelMenu.getAccessibleContext().setAccessibleDescription(
//                "Управление режимом отображения приложения");
//
//        {
//            JMenuItem systemLookAndFeel = new JMenuItem("Системная схема", KeyEvent.VK_S);
//            systemLookAndFeel.addActionListener((event) -> {
//                setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//                this.invalidate();
//            });
//            lookAndFeelMenu.add(systemLookAndFeel);
//        }
//
//        {
//            JMenuItem crossplatformLookAndFeel = new JMenuItem("Универсальная схема", KeyEvent.VK_S);
//            crossplatformLookAndFeel.addActionListener((event) -> {
//                setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
//                this.invalidate();
//            });
//            lookAndFeelMenu.add(crossplatformLookAndFeel);
//        }
//
//        JMenu testMenu = new JMenu("Тесты");
//        testMenu.setMnemonic(KeyEvent.VK_T);
//        testMenu.getAccessibleContext().setAccessibleDescription(
//                "Тестовые команды");
//
//        {
//            JMenuItem addLogMessageItem = new JMenuItem("Сообщение в лог", KeyEvent.VK_S);
//            addLogMessageItem.addActionListener((event) -> {
//                Logger.debug("Новая строка");
//            });
//            testMenu.add(addLogMessageItem);
//        }
//
//        menuBar.add(lookAndFeelMenu);
//        menuBar.add(testMenu);
//        return menuBar;
//    }

    private void setLookAndFeel(String className)
    {
        try
        {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        }
        catch (ClassNotFoundException | InstantiationException
            | IllegalAccessException | UnsupportedLookAndFeelException e)
        {
            // just ignore
        }
    }
}
