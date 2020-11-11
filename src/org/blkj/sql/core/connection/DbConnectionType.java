package org.blkj.sql.core.connection;

/**
 * 数据库连接类型； ProcessVO，Db、DbFree、DbConfigManager共享
 */
public interface DbConnectionType 
{
   /**
     * 使用用户给定的数据库连接对象，无配置文件
     */
    public static final int USING_CONFIG_OF_NONE = 0;
    
     //////////////////2016
    
    
    /**
     * 使用PVO配置文件，获取连接;配置文件/META-INF/db.xml
     */
    public static final int USING_CONFIG_OF_DEFAULT = 1;
    /**
     * //使用JPA配置文件，获取连接;配置文件/META-INF/persistence.xml
     */
    public static final int USING_CONFIG_OF_JPA = 2;
    /**
     * 使用HIBERNATE配置文件，获取连接；配置文件使用/META-INF/hibernate.xml
     */
    public static final int USING_CONFIG_OF_HIBERNATE = 3;
    /**
     * 使用iBatis/MYBATIS配置文件，获取连接；配置文件使用/META-INF/ibatis.xml配置,
     */
    public static final int USING_CONFIG_OF_MYBATIS = 4;
 
    public static final int USING_DB_01 = 101;//使用db_01，获取连接
    public static final int USING_DB_02 = 102;//使用db_02，获取连接
}
