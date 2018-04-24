package cn.partner4java.dao;  
  
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.transaction.annotation.Transactional;  
  
/** 
* @author partner4java 
* 使用JDBC Template处理数据 
http://irfen.me/use-jdbc-template-manipulation-data/
* 
*/ 
public class HelloDaoImpl extends JdbcDaoSupport {  
    
    @Transactional  
    public void saveHello(String tablename){  
    	List<String> listBusiness = getBusinessId();
    	 final List<Object[]> updateParams = new ArrayList<Object[]>();
    	List<Map<String, Object>> cis = this.getJdbcTemplate().queryForList("select * from "+tablename); 
    	for (Map<String, Object> map : cis) {
			String cisysid = (String) map.get("sys_id");
			java.util.Random random=new java.util.Random();// 定义随机类
			int result=random.nextInt(listBusiness.size());// 
			String businessid = listBusiness.get(result);
			updateParams.add(new Object[] { businessid, cisysid });
		}
    	addCIs(tablename, updateParams);
    	addCIs("cmdb_ci", updateParams);
    }
    public void addCIs(String tablename , final List<Object[]> updateParams )  
    {  
       String sql = "update "+ tablename + " set cmdb_ci_business=? where sys_id=?";  
       this.getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter()  
       {  
      
        @Override  
        public int getBatchSize() {  
         return updateParams.size();  
        }  

		@Override
		public void setValues(PreparedStatement ps, int i) throws SQLException {
			 Object[] args = updateParams.get(i);
	            ps.setString(1, (String) args[0]);
	            ps.setString(2, (String) args[1]);
			
		}  
       });  
    } 
	private List<String> getBusinessId() {
		List<String> listBusiness = new ArrayList<>();
    	List<Map<String, Object>> cis = this.getJdbcTemplate().queryForList("select * from cmdb_ci_business"); 
    	for (Map<String, Object> map : cis) {
    		listBusiness.add((String)map.get("sys_id"));
		}
    	return listBusiness;
	}  
    public static void main(String[] args) {
    	ApplicationContext ac = new ClassPathXmlApplicationContext("app-context.xml");  
    	HelloDaoImpl helloDao = (HelloDaoImpl) ac.getBean("helloDao");  
    	List<String> tableList = new ArrayList<>();
    	tableList.add("cmdb_ci_virtual");
    	tableList.add("cmdb_ci_host");
    	tableList.add("cmdb_ci_kafka");
    	tableList.add("zookeeper");
    	tableList.add("cmdb_ci_hbase");
    	tableList.add("cmdb_ci_hadoop");
    	tableList.add("cmdb_ci_NameNode");
    	tableList.add("cmdb_ci_DataNode"); 
//    	tableList.add("cmdb_ci");
    	
    	for (String table : tableList) {
    		helloDao.saveHello(table); 
		}
	}
} 