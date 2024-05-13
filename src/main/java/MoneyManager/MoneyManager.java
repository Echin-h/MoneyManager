package MoneyManager;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

public class MoneyManager {
    public static void main(String[] args) {
        LoginFrame lf=new LoginFrame();
        lf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}

//登录界面
class LoginFrame extends JFrame implements ActionListener{
    private JLabel l_user,l_pwd; //用户名标签，密码标签
    private JTextField t_user;//用户名文本框
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
            this.dispose();
            //添加退出代码
        }else if(b_ok==e.getSource()){
            //添加代码，验证身份成功后显示主界面
            String name = t_user.getText().trim();
            String pwd = t_pwd.getText().trim();
            if(name.equals("Tom")&pwd.equals("123")){

            }
            new MainFrame(t_user.getText().trim());

        }
    }
}



//JOptionPane.showMessageDialog(null,"用户名密码出错", "警告", //JOptionPane.ERROR_MESSAGE);


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
        l_ps = new JLabel("注意：时间格式为YYYYMMDD，例如：20150901");
        p_condition=new JPanel();
        p_condition.setLayout(new GridLayout(3,1));
        p_condition.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("输入查询条件"),
                BorderFactory.createEmptyBorder(5,5,5,5)));

        JPanel p1 = new JPanel();
        JPanel p2 = new JPanel();
        JPanel p3 = new JPanel();
        p1.add(l_type);
        p1.add(c_type);
        p1.add(b_select1);
        p2.add(l_fromdate);
        p2.add(t_fromdate);
        p2.add(l_todate);
        p2.add(t_todate);
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

        //添加代码

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

    public void actionPerformed(ActionEvent e) {
        Object temp=e.getSource();
        if(temp==mI[0]){
            new ModifyPwdFrame(username);
        }else if(temp==mI[1]){
            //添加代码
        }else if(temp==m_FMEdit){
            new BalEditFrame();
        }else if(temp==b_select1){  //根据收支类型查询
            //添加代码
        }else if(temp==b_select2){   //根据时间范围查询
            //添加代码
        }
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
            //添加代码
        }else if(b_ok==e.getSource()){  //修改密码
            //添加代码
        }
    }
}
//收支编辑界面
class BalEditFrame extends JFrame implements ActionListener{
    private JLabel l_id,l_date,l_bal,l_type,l_item;
    private JTextField t_id,t_date,t_bal;
    private JComboBox c_type,c_item;
    private JButton b_update,b_delete,b_select,b_new,b_clear;
    private JPanel p1,p2,p3;
    private JScrollPane scrollpane;
    private JTable table;

    public BalEditFrame(){
        super("收支编辑" );
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

        //添加代码，为table添加鼠标点击事件监听addMouseListener

        this.setResizable(false);
        this.setSize(800,300);
        Dimension screen = this.getToolkit().getScreenSize();
        this.setLocation((screen.width-this.getSize().width)/2,(screen.height-this.getSize().height)/2);
        this.show();
    }
    public void actionPerformed(ActionEvent e) {
        if(b_select==e.getSource()){  //查询所有收支信息
            //添加代码
        }else if(b_update==e.getSource()){  // 修改某条收支信息
            //添加代码
        }else if(b_delete==e.getSource()){   //删除某条收支信息
            //添加代码
        }else if(b_new==e.getSource()){   //新增某条收支信息
            //添加代码
        }else if(b_clear==e.getSource()){   //清空输入框
            //添加代码
        }
    }
}
 