package MoneyManager;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.*;
import java.text.SimpleDateFormat;
import javax.swing.*;
import javax.swing.*;
import java.util.Date;
import java.util.Calendar;

public class MoneyManager {
    public static void main(String[] args) {
        DBUtil db = new DBUtil();
        LoginFrame lf=new LoginFrame();
        lf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}

//登录界面
class LoginFrame extends JFrame implements ActionListener{
    private JLabel l_user,l_pwd; //用户名标签，密码标签
    private static JTextField t_user;//用户名文本框
    private JPasswordField t_pwd; //密码文本框
    private JButton b_ok,b_cancel; //登录按钮，退出按钮

    public LoginFrame(){
        super("欢迎使用个人理财账本!");
        l_user=new JLabel("用户名：",JLabel.RIGHT);
        l_pwd=new JLabel(" 密码：",JLabel.RIGHT);
        t_user=new JTextField(31);
        t_pwd=new JPasswordField(31);
        b_ok=new JButton("登录");
        b_cancel=new JButton("退出");
        //布局方式FlowLayout，一行排满排下一行
        Container c=this.getContentPane();
        c.setLayout(new FlowLayout());
        c.add(l_user);
        c.add(t_user);
        c.add(l_pwd);
        c.add(t_pwd);
        c.add(b_ok);
        c.add(b_cancel);
        //为按钮添加监听事件
        b_ok.addActionListener(this);
        b_cancel.addActionListener(this);
        //界面大小不可调整 
        this.setResizable(false);
        this.setSize(455,150);

        //界面显示居中
        Dimension screen = this.getToolkit().getScreenSize();
        this.setLocation((screen.width-this.getSize().width)/2,(screen.height-this.getSize().height)/2);
        this.show();
    }
    public void actionPerformed(ActionEvent e) {
        if(b_cancel==e.getSource()){
            //添加退出代码
            DBUtil.closeConnection(DBUtil.conn,DBUtil.stmt,DBUtil.rs);  //  关闭数据库连接
            this.dispose();
        }else if(b_ok==e.getSource()){
            //添加代码，验证身份成功后显示主界面
            String name = t_user.getText().trim();
            String pwd = t_pwd.getText().trim();
            String sql = "select * from user where username = ?";
            try {
                PreparedStatement pstmt = DBUtil.conn.prepareStatement(sql);
                pstmt.setString(1, name);
                ResultSet rs = pstmt.executeQuery();
                if (!rs.next()){
                    // 用户不存在，创建用户
                    // 图形界面没有注册的功能，所以直接--如果用户名不存在就创建用户 哈哈哈哈哈
                    String sql1 = "insert into user(username, password) values(?, ?)";
                    PreparedStatement pstmt1 = DBUtil.conn.prepareStatement(sql1);
                    pstmt1.setString(1, name);
                    pstmt1.setString(2, pwd);
                    //检验密码必须包含数字，字母，以及大于八位
                    if (pwd.length() < 8 || !pwd.matches(".*[a-zA-Z].*") || !pwd.matches(".*[0-9].*")){
                        JOptionPane.showMessageDialog(null, "密码必须包含数字，字母，以及大于八位", "警告", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    pstmt1.executeUpdate();
                    JOptionPane.showMessageDialog(null, "用户创建成功", "提示", JOptionPane.INFORMATION_MESSAGE);
                    new MainFrame(t_user.getText().trim());
                    this.dispose();
                } else {
                    // 用户存在，验证密码
                    if (rs.getString("password").equals(pwd)) {
                        JOptionPane.showMessageDialog(null, "登录成功", "提示", JOptionPane.INFORMATION_MESSAGE);
                        new MainFrame(t_user.getText().trim());
                        this.dispose();
                    } else {
                        JOptionPane.showMessageDialog(null, "用户名密码出错", "警告", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }
    public static String Getname(){
        return t_user.getText().trim();

    }
}

//主界面
class MainFrame extends JFrame implements ActionListener{
    private JMenuBar mb=new JMenuBar();
    private JMenu m_system=new JMenu("系统管理");
    private JMenu m_fm=new JMenu("收支管理");
    private JMenuItem mI[]={new JMenuItem("密码重置"),new JMenuItem("退出系统")};
    private JMenuItem m_FMEdit=new JMenuItem("收支编辑");
    private JLabel l_type,l_fromdate,l_todate,l_bal,l_ps;
    private JTextField t_fromdate,t_todate;
    private JButton b_select1,b_select2;
    private JComboBox c_type;
    private JPanel p_condition,p_detail;
    private String s1[]={"收入","支出"};
    private double bal1,bal2;
    private JTable table;
    private String username;
    private JSpinner spinnerFromDate = new JSpinner();
    private JSpinner spinnerToDate = new JSpinner();

//    private JSpinner spinnerFromDate = new JSpinner();
//
//    private JSpinner spinnerToDate = new JSpinner();
    public MainFrame(String username){
        super(username+",欢迎使用个人理财账本!");
        this.username=username;
        Container c=this.getContentPane();
        c.setLayout(new BorderLayout());
        c.add(mb,"North");
        mb.add(m_system);
        mb.add(m_fm);
        m_system.add(mI[0]);
        m_system.add(mI[1]);
        m_fm.add(m_FMEdit);
        m_FMEdit.addActionListener(this);
        mI[0].addActionListener(this);
        mI[1].addActionListener(this);

        l_type=new JLabel("收支类型：");
        c_type=new JComboBox(s1);
        b_select1=new JButton("查询");
        l_fromdate=new JLabel("起始时间");
        t_fromdate=new JTextField(8);
        l_todate=new JLabel("终止时间");
        t_todate=new JTextField(8);
        b_select2=new JButton("查询");
        l_ps = new JLabel("注意：时间格式为YYYY-MM-DD，例如：2015-09-01 ");
        p_condition=new JPanel();
        p_condition.setLayout(new GridLayout(3,1));
        p_condition.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("输入查询条件"),
                BorderFactory.createEmptyBorder(5,5,5,5)));

        JFrame frame = new JFrame("Date Spinner Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(200, 200);

//        // 创建 SpinnerDateModel
//        Date initialDate = new Date();
//        Calendar calendar = Calendar.getInstance();
//        calendar.add(Calendar.YEAR, -100);
//        Date earliestDate = calendar.getTime();
//        calendar.add(Calendar.YEAR, 200);
//        Date latestDate = calendar.getTime();
//        SpinnerDateModel modelFromDate = new SpinnerDateModel(initialDate, earliestDate, latestDate, Calendar.YEAR);
//        SpinnerDateModel modelToDate = new SpinnerDateModel(initialDate, earliestDate, latestDate, Calendar.YEAR);
//
//        // 创建 JSpinner 并设置 model
//        JSpinner spinnerFromDate = new JSpinner(modelFromDate);
//        JSpinner spinnerToDate = new JSpinner(modelToDate);
//
//        JSpinner.DateEditor editorFrom = new JSpinner.DateEditor(spinnerFromDate, "yyyy-MM-dd");
//        JSpinner.DateEditor editorTo = new JSpinner.DateEditor(spinnerToDate, "yyyy-MM-dd");
//        spinnerFromDate.setEditor(editorFrom);
//        spinnerToDate.setEditor(editorTo);

        SpinnerDateModel modelFrom = new SpinnerDateModel();
        SpinnerDateModel modelTo = new SpinnerDateModel();
        modelFrom.setValue(new Date()); // 设置当前日期为起始日期
        modelTo.setValue(new Date());   // 设置当前日期为终止日期

// 创建 JSpinner 实例并设置模型
         spinnerFromDate = new JSpinner(modelFrom);
         spinnerToDate = new JSpinner(modelTo);

// 创建 JSpinner.DateEditor 实例并设置日期格式
        JSpinner.DateEditor editorFrom = new JSpinner.DateEditor(spinnerFromDate, "yyyy-MM-dd");
        JSpinner.DateEditor editorTo = new JSpinner.DateEditor(spinnerToDate, "yyyy-MM-dd");
        spinnerFromDate.setEditor(editorFrom);
        spinnerToDate.setEditor(editorTo);

        JPanel p1 = new JPanel();
        JPanel p2 = new JPanel();
        JPanel p3 = new JPanel();
        p1.add(l_type);
        p1.add(c_type);
        p1.add(b_select1);
        p2.add(l_fromdate);
        // 日历的添加
        p2.add(l_fromdate);
        p2.add(spinnerFromDate); // 添加起始日期选择器
        p2.add(l_todate);
        p2.add(spinnerToDate);
//        p2.add(t_todate);
        p2.add(b_select2);
        p3.add(l_ps);
        p_condition.add(p1);
        p_condition.add(p2);
        p_condition.add(p3);
        c.add(p_condition,"Center");

        b_select1.addActionListener(this);
        b_select2.addActionListener(this);

        p_detail=new JPanel();
        p_detail.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("收支明细信息"),
                BorderFactory.createEmptyBorder(5,5,5,5)));
        l_bal=new JLabel();
        String[] cloum = {"编号", "日期", "类型","内容","金额",};
        Object[][] row = new Object[50][5];
        table = new JTable(row, cloum);
        JScrollPane scrollpane = new JScrollPane(table);
        scrollpane.setPreferredSize(new Dimension(580,350));
        scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollpane.setViewportView(table);
        p_detail.add(l_bal);
        p_detail.add(scrollpane);
        c.add(p_detail,"South");

        // 就是把数据从数据库中取出来然后填充到这个mainFrame中，可以保证每次打开这个界面都是最新的数据
        // 具体的代码就直接看登录的sql细节，换成select而已
        // 代码如下：
            String sql="select * from balance where username = ?";
            try {
                PreparedStatement pstmt = DBUtil.conn.prepareStatement(sql);
                pstmt.setString(1, this.username);
                ResultSet rs = pstmt.executeQuery();
                int i = 0;
                while (rs.next()) {
                    table.setValueAt(rs.getString("id"), i, 0);
                    table.setValueAt(rs.getString("date"), i, 1);
                    table.setValueAt(rs.getString("type"), i, 2);
                    table.setValueAt(rs.getString("item"), i, 3);
                    table.setValueAt(rs.getString("money"), i, 4);
                    i++;
                }
            } catch (SQLException e1) {
                e1.printStackTrace();
            }

        if(bal1<0)
            l_bal.setText("个人总收支余额为"+bal1+"元。您已超支，请适度消费！");
        else
            l_bal.setText("个人总收支余额为"+bal1+"元。");

        this.setResizable(false);
        this.setSize(600,580);
        Dimension screen = this.getToolkit().getScreenSize();
        this.setLocation((screen.width-this.getSize().width)/2,(screen.height-this.getSize().height)/2);
        this.show();
    }

    public void reflash(){
        //把所有的数据都清空
        for(int i=0;i<50;i++){
            for(int j=0;j<5;j++){
                table.setValueAt("", i, j);
            }
        }
    }
    public void actionPerformed(ActionEvent e) {
        Object temp=e.getSource();
        if(temp==mI[0]){
            new ModifyPwdFrame(username);   // 这里看看要不要再加代码了，感觉要加，懒得看了
        }else if(temp==mI[1]){    //  private JMenuItem mI[]={new JMenuItem("密码重置"),new JMenuItem("退出系统")}; ,一个是密码重置，一个是退出系统
            DBUtil.closeConnection(DBUtil.conn,DBUtil.stmt,DBUtil.rs);  //  关闭数据库连接
            this.dispose();
        }else if(temp==m_FMEdit){
            new BalEditFrame();
        }else if(temp==b_select1){  // 注意： private String s1[]={"收入","支出"};
            if(c_type.getSelectedItem().equals("收入")){  //查询收入信息
                    //刷新页面
                    reflash();
                //添加代码,使用sql语句查询收入信息，然后显示在table中
                    String sql="select * from balance where username = ? and type = ?";
                    try {
                        PreparedStatement pstmt = DBUtil.conn.prepareStatement(sql);
                        pstmt.setString(1, this.username);
                        pstmt.setString(2, "收入");
                        ResultSet rs = pstmt.executeQuery();
                        int i = 0;
                        while (rs.next()) {
                            table.setValueAt(rs.getString("id"), i, 0);
                            table.setValueAt(rs.getString("date"), i, 1);
                            table.setValueAt(rs.getString("type"), i, 2);
                            table.setValueAt(rs.getString("item"), i, 3);
                            table.setValueAt(rs.getString("money"), i, 4);
                            i++;
                        }
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
            }else if(c_type.getSelectedItem().equals("支出")) {  //查询支出信息
                //刷新页面
                reflash();
                //添加代码,使用sql语句查询支出信息，然后显示在table中
                String sql="select * from balance where username = ? and type = ?";
                try {
                    PreparedStatement pstmt = DBUtil.conn.prepareStatement(sql);
                    pstmt.setString(1, this.username);
                    pstmt.setString(2, "支出");
                    ResultSet rs = pstmt.executeQuery();
                    int i = 0;
                    while (rs.next()) {
                        table.setValueAt(rs.getString("id"), i, 0);
                        table.setValueAt(rs.getString("date"), i, 1);
                        table.setValueAt(rs.getString("type"), i, 2);
                        table.setValueAt(rs.getString("item"), i, 3);
                        table.setValueAt(rs.getString("money"), i, 4);
                        i++;
                    }
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        }else if(temp==b_select2){   //根据时间范围查询   // t_formdate, t_todate, 从这两个地方入手，记得查询的时候两个值都要用，哪怕为空
            reflash();
            String sql;
            String fromdate = getFormattedDate(spinnerFromDate);
            String todate = getFormattedDate(spinnerToDate);
            if (fromdate.compareTo(todate) > 0){
                JOptionPane.showMessageDialog(null, "起始时间不能大于终止时间", "警告", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if(fromdate.isEmpty()&&todate.isEmpty()){
                sql = "SELECT * FROM balance where username=?";
                try {
                    PreparedStatement pstmt = DBUtil.conn.prepareStatement(sql);
                    pstmt.setString(1, this.username);
                    ResultSet rs = pstmt.executeQuery();
                    int i = 0;
                    while (rs.next()) {
                        table.setValueAt(rs.getString("id"), i, 0);
                        table.setValueAt(rs.getString("date"), i, 1);
                        table.setValueAt(rs.getString("type"), i, 2);
                        table.setValueAt(rs.getString("item"), i, 3);
                        table.setValueAt(rs.getString("money"), i, 4);
                        i++;
                    }
                    //如果没有日期范围内的数据就显示所有数据


                } catch (SQLException e1) {
                    e1.printStackTrace();
                }

            }else {
                sql = "SELECT * FROM balance WHERE username = ? AND date BETWEEN ? AND ? ";
                try {
                    PreparedStatement pstmt = DBUtil.conn.prepareStatement(sql);
                    pstmt.setString(1, this.username);
                    pstmt.setString(2, fromdate);
                    pstmt.setString(3, todate);
                    ResultSet rs = pstmt.executeQuery();
                    int i = 0;
                    while (rs.next()) {
                        table.setValueAt(rs.getString("id"), i, 0);
                        table.setValueAt(rs.getString("date"), i, 1);
                        table.setValueAt(rs.getString("type"), i, 2);
                        table.setValueAt(rs.getString("item"), i, 3);
                        table.setValueAt(rs.getString("money"), i, 4);
                        i++;
                    }
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }

            }

        }
    }
    private String getFormattedDate(JSpinner spinner) {
        Date date = (Date) spinner.getValue();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }
}

//修改密码界面
class ModifyPwdFrame extends JFrame implements ActionListener{
    private JLabel l_oldPWD,l_newPWD,l_newPWDAgain;
    private JPasswordField t_oldPWD,t_newPWD,t_newPWDAgain;
    private JButton b_ok,b_cancel;
    private String username;

    public ModifyPwdFrame(String username){
        super("修改密码");
        this.username=username;
        l_oldPWD=new JLabel("旧密码");
        l_newPWD=new JLabel("新密码：");
        l_newPWDAgain=new JLabel("确认新密码：");
        t_oldPWD=new JPasswordField(15);
        t_newPWD=new JPasswordField(15);
        t_newPWDAgain=new JPasswordField(15);
        b_ok=new JButton("确定");
        b_cancel=new JButton("取消");
        Container c=this.getContentPane();
        c.setLayout(new FlowLayout());
        c.add(l_oldPWD);
        c.add(t_oldPWD);
        c.add(l_newPWD);
        c.add(t_newPWD);
        c.add(l_newPWDAgain);
        c.add(t_newPWDAgain);
        c.add(b_ok);
        c.add(b_cancel);
        b_ok.addActionListener(this);
        b_cancel.addActionListener(this);
        this.setResizable(false);
        this.setSize(280,160);
        Dimension screen = this.getToolkit().getScreenSize();
        this.setLocation((screen.width-this.getSize().width)/2,(screen.height-this.getSize().height)/2);
        this.show();
    }

    public void actionPerformed(ActionEvent e) {
        if(b_cancel==e.getSource()){
            // 取消修改密码噢
            JOptionPane.showMessageDialog(null,"密码修改取消！", "提示", JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
        }else if(b_ok==e.getSource()){
            // 密码修改
            String oldPwd = t_oldPWD.getText().trim();
            String newPwd = t_newPWD.getText().trim();
            String newPwdAgain = t_newPWDAgain.getText().trim();
            if (newPwd.isEmpty()){
                JOptionPane.showMessageDialog(null,"新密码不能为空！", "警告", JOptionPane.ERROR_MESSAGE);
            } else if (newPwd.length() < 8 || !newPwd.matches(".*[a-zA-Z].*") || !newPwd.matches(".*[0-9].*")){
                JOptionPane.showMessageDialog(null, "密码必须包含数字，字母，以及大于八位", "警告", JOptionPane.ERROR_MESSAGE);
            } else if(newPwd.equals(oldPwd)){
                JOptionPane.showMessageDialog(null,"新密码不能与旧密码相同！", "警告", JOptionPane.ERROR_MESSAGE);
            }else if(!newPwd.equals(newPwdAgain)){
                JOptionPane.showMessageDialog(null,"两次输入的新密码不一致！", "警告", JOptionPane.ERROR_MESSAGE);
            }else {
                String sql = "select * from user where username = ?";
                try {
                    PreparedStatement pstmt = DBUtil.conn.prepareStatement(sql);
                    pstmt.setString(1, username);
                    ResultSet rs = pstmt.executeQuery();
                    if (rs.next()){
                        if (rs.getString("password").equals(oldPwd)){
                            String sql1 = "update user set password = ? where username = ?";
                            PreparedStatement pstmt1 = DBUtil.conn.prepareStatement(sql1);
                            pstmt1.setString(1, newPwd);
                            pstmt1.setString(2, username);
                            pstmt1.executeUpdate();
                            JOptionPane.showMessageDialog(null,"密码修改成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
                            this.dispose();
                        }else {
                            JOptionPane.showMessageDialog(null,"旧密码错误！", "警告", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
}

//收支编辑界面
class BalEditFrame extends JFrame implements ActionListener{
    private String username;
    private JLabel l_id,l_date,l_bal,l_type,l_item;
    private JTextField t_id,t_date,t_bal;
    private JComboBox c_type,c_item;
    private JButton b_update,b_delete,b_select,b_new,b_clear;
    private JPanel p1,p2,p3;
    private JScrollPane scrollpane;
    private JTable table;

    public BalEditFrame(){
        super("收支编辑" );
        this.username = LoginFrame.Getname();
        l_id=new JLabel("编号：");
        l_date=new JLabel("日期：");
        l_bal=new JLabel("金额：");
        l_type=new JLabel("类型：");
        l_item=new JLabel("内容：");
        t_id=new JTextField(8);
        t_date=new JTextField(8);
        t_bal=new JTextField(8);

        String s1[]={"收入","支出"};
        String s2[]={"购物","餐饮","居家","交通","娱乐","人情","工资","奖金","其他"};
        c_type=new JComboBox(s1);
        c_item=new JComboBox(s2);
        c_type.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    c_item.removeAllItems();
                    if (c_type.getSelectedItem().equals("收入")) {
                        c_item.addItem("工资");
                        c_item.addItem("奖金");
                        c_item.addItem("其他");
                    } else if (c_type.getSelectedItem().equals("支出")) {
                        c_item.addItem("购物");
                        c_item.addItem("餐饮");
                        c_item.addItem("居家");
                        c_item.addItem("交通");
                        c_item.addItem("娱乐");
                        c_item.addItem("人情");
                        c_item.addItem("其他");
                    }
                }
            }
        });

        b_select=new JButton("查询");
        b_update=new JButton("修改");
        b_delete=new JButton("删除");
        b_new=new JButton("录入");
        b_clear=new JButton("清空");

        Container c=this.getContentPane();
        c.setLayout(new BorderLayout());

        p1=new JPanel();
        p1.setLayout(new GridLayout(5,2,10,10));
        p1.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("编辑收支信息"),
                BorderFactory.createEmptyBorder(5,5,5,5)));
        p1.add(l_id);
        p1.add(t_id);
        p1.add(l_date);
        p1.add(t_date);
        p1.add(l_type);
        p1.add(c_type);
        p1.add(l_item);
        p1.add(c_item);
        p1.add(l_bal);
        p1.add(t_bal);
        c.add(p1, BorderLayout.WEST);

        p2=new JPanel();
        p2.setLayout(new GridLayout(5,1,10,10));
        p2.add(b_new);
        p2.add(b_update);
        p2.add(b_delete);
        p2.add(b_select);
        p2.add(b_clear);

        c.add(p2,BorderLayout.CENTER);

        p3=new JPanel();
        p3.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("显示收支信息"),
                BorderFactory.createEmptyBorder(5,5,5,5)));

        String[] cloum = { "编号", "日期", "类型","内容", "金额"};
        Object[][] row = new Object[50][5];
        table = new JTable(row, cloum);
        scrollpane = new JScrollPane(table);
        scrollpane.setViewportView(table);
        scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        p3.add(scrollpane);
        c.add(p3,BorderLayout.EAST);

        b_update.addActionListener(this);
        b_delete.addActionListener(this);
        b_select.addActionListener(this);
        b_new.addActionListener(this);
        b_clear.addActionListener(this);
        //键鼠事件
        table.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e){
                int row = table.getSelectedRow();
                t_id.setText(table.getValueAt(row, 0).toString());
                t_date.setText(table.getValueAt(row, 1).toString());
                c_type.setSelectedItem(table.getValueAt(row, 2).toString());
                c_item.setSelectedItem(table.getValueAt(row, 3).toString());
                t_bal.setText(table.getValueAt(row, 4).toString());
            }
        });

        this.setResizable(false);
        this.setSize(800,300);
        Dimension screen = this.getToolkit().getScreenSize();
        this.setLocation((screen.width-this.getSize().width)/2,(screen.height-this.getSize().height)/2);
        this.show();
    }


    public void refreshTable() {
        //清空表格
        for (int i = 0; i < 50; i++) {
            for (int j = 0; j < 5; j++) {
                table.setValueAt("", i, j);
            }
        }
        String sql1="select * from balance where username = ?";
        try {
            PreparedStatement pstmt = DBUtil.conn.prepareStatement(sql1);
            pstmt.setString(1, this.username);
            ResultSet rs = pstmt.executeQuery();
            int i = 0;
            while (rs.next()) {
                table.setValueAt(rs.getString("id"), i, 0);
                table.setValueAt(rs.getString("date"), i, 1);
                table.setValueAt(rs.getString("type"), i, 2);
                table.setValueAt(rs.getString("item"), i, 3);
                table.setValueAt(rs.getString("money"), i, 4);
                i++;
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    public void actionPerformed(ActionEvent e) {
        if(b_select==e.getSource()){  //查询所有收支信息
            refreshTable();
        }else if(b_update==e.getSource()){  // 修改某条收支信息
            // 根据某一行的编号，修改这一行的数据
            // String id = t_id.getText().trim(); 这样子就可以根据编号修改了
            int row = table.getSelectedRow();
            String id = table.getValueAt(row, 0).toString();
            String date = t_date.getText().trim();
            String type = c_type.getSelectedItem().toString();
            String item = c_item.getSelectedItem().toString();
            String money = t_bal.getText().trim();
            String sql = "update balance set date = ?, type = ?, item = ?, money = ? where id = ?";
            try {
                PreparedStatement pstmt = DBUtil.conn.prepareStatement(sql);
                pstmt.setString(1, date);
                pstmt.setString(2, type);
                pstmt.setString(3, item);
                pstmt.setString(4, money);
                pstmt.setString(5, id);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(null, "修改成功", "提示", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e1) {
                e1.printStackTrace();
            }finally {
                //刷新页面,和查询同理
                refreshTable();

            }

        }else if(b_delete==e.getSource()){   //删除某条收支信息
            //添加代码,删除鼠标选中的行
            // String id = t_id.getText().trim(); 这样子就可以根据编号删除了
            int row = table.getSelectedRow();
            String id = table.getValueAt(row, 0).toString();
            String sql = "delete from balance where id = ?";
            try {
                PreparedStatement pstmt = DBUtil.conn.prepareStatement(sql);
                pstmt.setString(1, id);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(null, "删除成功", "提示", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e1) {
                e1.printStackTrace();
            }finally {
                //刷新页面,和查询同理
                refreshTable();
            }
        }else if(b_new==e.getSource()){   //新增某条收支信息
            String id = t_id.getText().trim();
            String date = t_date.getText().trim();
            String type = c_type.getSelectedItem().toString();
            String item = c_item.getSelectedItem().toString();
            String money = t_bal.getText().trim();
            //如果id是空的话，自增
            if (id.isEmpty()) {
                String sql = "insert into balance( date, type, item, money,username) values( ?, ?, ?, ?,?)";
                try {
                    PreparedStatement pstmt = DBUtil.conn.prepareStatement(sql);
                    pstmt.setString(1, date);
                    pstmt.setString(2, type);
                    pstmt.setString(3, item);
                    pstmt.setString(4, money);
                    pstmt.setString(5, this.username);
                    if (date.length() != 10 || Integer.parseInt(date.substring(0, 4)) < 1000 || Integer.parseInt(date.substring(0, 4)) > 9999 || Integer.parseInt(date.substring(5, 7)) < 1 || Integer.parseInt(date.substring(5, 7)) > 12 || Integer.parseInt(date.substring(8, 10)) < 1 || Integer.parseInt(date.substring(8, 10)) > 31){
                        JOptionPane.showMessageDialog(null, "日期格式错误", "错误", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    pstmt.executeUpdate();
                    JOptionPane.showMessageDialog(null, "新增成功", "提示", JOptionPane.INFORMATION_MESSAGE);
                }catch (SQLException e2){
                    e2.printStackTrace();
                    //弹窗提示
                    JOptionPane.showMessageDialog(null, "请检查你的输入是否正确", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }else{
                String sql = "insert into balance(id, date, type, item, money,username) values(?, ?, ?, ?, ?,?)";
                try {
                    PreparedStatement pstmt = DBUtil.conn.prepareStatement(sql);
                    pstmt.setString(1, id);
                    pstmt.setString(2, date);
                    pstmt.setString(3, type);
                    pstmt.setString(4, item);
                    pstmt.setString(5, money);
                    pstmt.setString(6, this.username);
                    pstmt.executeUpdate();
                    JOptionPane.showMessageDialog(null, "新增成功", "提示", JOptionPane.INFORMATION_MESSAGE);
                }catch (SQLIntegrityConstraintViolationException e1) {
                    JOptionPane.showMessageDialog(null, "新增失败，ID已存在", "错误", JOptionPane.ERROR_MESSAGE);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
            refreshTable();
        }else if(b_clear==e.getSource()){   //清空输入框
            //添加代码
            t_id.setText("");
            t_date.setText("");
            t_bal.setText("");
        }
    }
}

class DBUtil{
    public static Connection conn=null;
    public static Statement stmt=null;
    static ResultSet rs=null;
    private static String driver="com.mysql.cj.jdbc.Driver";
    private static String url="jdbc:mysql://localhost:3307/moneymanager";
    private static String user="user";
    private static String password="password";

    public DBUtil(){
        conn=getConnection();
        try{
            stmt=conn.createStatement();
            migrate();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static Connection getConnection(){
        try{
            Class.forName(driver);
            conn=DriverManager.getConnection(url,user,password);
        }catch(Exception e){
            e.printStackTrace();
        }
        return conn;
    }

    public static void closeConnection(Connection conn,Statement stmt,ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 这里创建表的方式非常的死板，但是为了简单，就这样了
    public static void migrate() {
        // 创建一个user表
        String sqlU = "create table if not exists user(id int primary key auto_increment, username varchar(20), password varchar(20))";
        try {
            stmt.executeUpdate(sqlU);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 创建一个balance表  (编号，日期，类型，内容，金额)
        // 有想过把时间的格式改成时间戳，然后每次用户记录账单的时候，直接用系统时间戳，这样就不用用户输入时间了，但是这个样子用户就会很局限于这个系统的时间，所以还是用用户输入时间吧
        String sqlB = "create table if not exists balance(id int primary key auto_increment, date date, type varchar(20), item varchar(20), money double,username varchar(20))";
        try {
            stmt.executeUpdate(sqlB);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

class TestDBUtil{
    public static void main(String[] args){
        new DBUtil();
        System.out.println("数据库连接成功！");
    }
}

