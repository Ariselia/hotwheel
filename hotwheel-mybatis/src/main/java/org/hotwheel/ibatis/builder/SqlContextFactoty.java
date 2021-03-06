package org.hotwheel.ibatis.builder;

import org.apache.ibatis.builder.BaseBuilder;
import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.datasource.DataSourceFactory;
import org.apache.ibatis.executor.loader.ProxyFactory;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.session.*;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.type.JdbcType;

import java.util.Properties;

/**
 * Created by wangfeng on 2016/11/6.
 */
public class SqlContextFactoty extends BaseBuilder{
    private Properties properties = null;

    public SqlContextFactoty() {
        super(new Configuration());
        //properties = defaultProperties();
        this.properties = configuration.getVariables();
    }

    public SqlContextFactoty(Configuration configuration) {
        super(configuration);
        this.properties = configuration.getVariables();
    }

    public SqlContextFactoty(Properties props) {
        super(new Configuration());
        this.properties = props;
        defaultConfiguration(props);
    }
/*
    private Properties defaultProperties1() {
        Properties props = new Properties();
        props.setProperty("autoMappingBehavior", "PARTIAL");
        props.setProperty("autoMappingUnknownColumnBehavior", "NONE");
        props.setProperty("cacheEnabled", "true");
        //props.setProperty("proxyFactory", null);
        props.setProperty("lazyLoadingEnabled", "false");
        props.setProperty("aggressiveLazyLoading", "true");
        props.setProperty("multipleResultSetsEnabled", "true");
        props.setProperty("useColumnLabel", "true");
        props.setProperty("useGeneratedKeys", "false");
        props.setProperty("defaultExecutorType", "SIMPLE");
        //props.getProperty("defaultStatementTimeout", null);
        //props.getProperty("defaultFetchSize", null);
        props.setProperty("mapUnderscoreToCamelCase", "false");
        props.setProperty("safeRowBoundsEnabled", "false");
        props.setProperty("localCacheScope", "SESSION");
        props.setProperty("jdbcTypeForNull", "OTHER");
        props.setProperty("lazyLoadTriggerMethods", "equals,clone,hashCode,toString");
        props.setProperty("safeResultHandlerEnabled", "true");
        //props.getProperty("defaultScriptingLanguage", null);
        props.setProperty("callSettersOnNulls", "false");
        props.setProperty("useActualParamName", "false");
        //props.getProperty("logPrefix", null);
        props.setProperty("logImpl", "log4j2");
        //props.setProperty("configurationFactory", null);
        return props;
    }
*/
    public Configuration defaultConfiguration(Properties props) {
        if(props == null) {
            props = properties;
        }
        configuration.setAutoMappingBehavior(AutoMappingBehavior.valueOf(props.getProperty("autoMappingBehavior", "PARTIAL")));
        configuration.setAutoMappingUnknownColumnBehavior(AutoMappingUnknownColumnBehavior.valueOf(props.getProperty("autoMappingUnknownColumnBehavior", "NONE")));
        configuration.setCacheEnabled(booleanValueOf(props.getProperty("cacheEnabled"), true));
        configuration.setProxyFactory((ProxyFactory) createInstance(props.getProperty("proxyFactory")));
        configuration.setLazyLoadingEnabled(booleanValueOf(props.getProperty("lazyLoadingEnabled"), false));
        configuration.setAggressiveLazyLoading(booleanValueOf(props.getProperty("aggressiveLazyLoading"), true));
        configuration.setMultipleResultSetsEnabled(booleanValueOf(props.getProperty("multipleResultSetsEnabled"), true));
        configuration.setUseColumnLabel(booleanValueOf(props.getProperty("useColumnLabel"), true));
        configuration.setUseGeneratedKeys(booleanValueOf(props.getProperty("useGeneratedKeys"), false));
        configuration.setDefaultExecutorType(ExecutorType.valueOf(props.getProperty("defaultExecutorType", "SIMPLE")));
        configuration.setDefaultStatementTimeout(integerValueOf(props.getProperty("defaultStatementTimeout"), null));
        configuration.setDefaultFetchSize(integerValueOf(props.getProperty("defaultFetchSize"), null));
        configuration.setMapUnderscoreToCamelCase(booleanValueOf(props.getProperty("mapUnderscoreToCamelCase"), false));
        configuration.setSafeRowBoundsEnabled(booleanValueOf(props.getProperty("safeRowBoundsEnabled"), false));
        configuration.setLocalCacheScope(LocalCacheScope.valueOf(props.getProperty("localCacheScope", "SESSION")));
        configuration.setJdbcTypeForNull(JdbcType.valueOf(props.getProperty("jdbcTypeForNull", "OTHER")));
        configuration.setLazyLoadTriggerMethods(stringSetValueOf(props.getProperty("lazyLoadTriggerMethods"), "equals,clone,hashCode,toString"));
        configuration.setSafeResultHandlerEnabled(booleanValueOf(props.getProperty("safeResultHandlerEnabled"), true));
        configuration.setDefaultScriptingLanguage(resolveClass(props.getProperty("defaultScriptingLanguage")));
        configuration.setCallSettersOnNulls(booleanValueOf(props.getProperty("callSettersOnNulls"), false));
        configuration.setUseActualParamName(booleanValueOf(props.getProperty("useActualParamName"), false));
        configuration.setLogPrefix(props.getProperty("logPrefix"));
        @SuppressWarnings("unchecked")
        Class<? extends Log> logImpl = (Class<? extends Log>)resolveClass(props.getProperty("logImpl"));
        configuration.setLogImpl(logImpl);
        configuration.setConfigurationFactory(resolveClass(props.getProperty("configurationFactory")));
        configuration.setVariables(props);
        return configuration;
    }

    public DataSourceFactory dataSourceElement(String type) throws Exception {
        if (type != null) {
            DataSourceFactory factory = (DataSourceFactory) resolveClass(type).newInstance();
            return factory;
        }
        throw new BuilderException("Environment declaration requires a DataSourceFactory.");
    }

    public TransactionFactory transactionManagerElement(String type) throws Exception {
        if (type != null) {
            TransactionFactory factory = (TransactionFactory) resolveClass(type).newInstance();
            return factory;
        }
        throw new BuilderException("Environment declaration requires a TransactionFactory.");
    }
}
