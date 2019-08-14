package com.gamingmesh.jobs.dao;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.YamlConfiguration;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.CMILib.ConfigReader;

public class JobsManager {
    private JobsDAO dao;
    private Jobs plugin;
    private DataBaseType DbType = DataBaseType.SqLite;

    public enum DataBaseType {
	MySQL, SqLite
    }

    public JobsManager(Jobs plugin) {
	this.plugin = plugin;
    }

    public JobsDAO getDB() {
	return dao;
    }

    public void switchDataBase() {
	if (dao != null)
	    dao.closeConnections();
	// Picking opposite database then it is currently
	switch (DbType) {
	case MySQL:
	    // If it MySQL lets change to SqLite
	    DbType = DataBaseType.SqLite;
	    dao = startSqlite();
	    dao.setDbType(DbType);
	    break;
	case SqLite:
	    // If it SqLite lets change to MySQL
	    DbType = DataBaseType.MySQL;
	    dao = startMysql();
	    dao.setDbType(DbType);
	    break;
	default:
	    break;
	}

	File f = new File(Jobs.getFolder(), "generalConfig.yml");
	YamlConfiguration config = YamlConfiguration.loadConfiguration(f);

	config.set("storage.method", DbType.toString().toLowerCase());
	try {
	    config.save(f);
	} catch (IOException e) {
	    e.printStackTrace();
	}

	Jobs.setDAO(dao);
    }

    String username = "root";
    String password = "";
    String hostname = "localhost:3306";
    String database = "minecraft";
    String prefix = "jobs_";
    boolean certificate = false;
    boolean ssl = false;
    boolean autoReconnect = false;

    public void start() {
	ConfigReader c = Jobs.getGCManager().getConfig();
	c.addComment("storage.method", "storage method, can be MySQL or sqlite");
	String storageMethod = c.get("storage.method", "sqlite");
	c.addComment("mysql", "Requires Mysql.");

	username = c.get("mysql.username", c.getC().getString("mysql-username", "root"));
	password = c.get("mysql.password", c.getC().getString("mysql-password", ""));
	hostname = c.get("mysql.hostname", c.getC().getString("mysql-hostname", "localhost:3306"));
	database = c.get("mysql.database", c.getC().getString("mysql-database", "minecraft"));
	prefix = c.get("mysql.table-prefix", c.getC().getString("mysql-table-prefix", "jobs_"));
	certificate = c.get("mysql.verify-server-certificate", c.getC().getBoolean("verify-server-certificate", false));
	ssl = c.get("mysql.use-ssl", c.getC().getBoolean("use-ssl", false));
	autoReconnect = c.get("mysql.auto-reconnect", c.getC().getBoolean("auto-reconnect", true));

	if (storageMethod.equalsIgnoreCase("mysql")) {
	    DbType = DataBaseType.MySQL;
	    dao = startMysql();
	} else if (storageMethod.equalsIgnoreCase("sqlite")) {
	    DbType = DataBaseType.SqLite;
	    dao = startSqlite();
	} else {
	    Jobs.consoleMsg("&cInvalid storage method! Changing method to sqlite!");
	    c.set("storage.method", "sqlite");
	    DbType = DataBaseType.SqLite;
	    dao = startSqlite();
	}
	Jobs.setDAO(dao);
    }

    private synchronized JobsMySQL startMysql() {

	ConfigReader c = Jobs.getGCManager().getConfig();

	String legacyUrl = c.getC().getString("mysql.url");
	if (legacyUrl != null) {
	    String jdbcString = "jdbc:mysql://";
	    if (legacyUrl.toLowerCase().startsWith(jdbcString)) {
		legacyUrl = legacyUrl.substring(jdbcString.length());
		String[] parts = legacyUrl.split("/");
		if (parts.length >= 2) {
		    hostname = c.get("mysql.hostname", parts[0]);
		    database = c.get("mysql.database", parts[1]);
		}
	    }
	}
	
	if (username == null) {
	    Jobs.getPluginLogger().severe("mysql username property invalid or missing");
	}

	if (plugin.isEnabled()) {
	    JobsMySQL data = new JobsMySQL(plugin, hostname, database, username, password, prefix, certificate, ssl, autoReconnect);
	    data.initialize();
	    return data;
	}
	return null;
    }

    private synchronized JobsSQLite startSqlite() {
	JobsSQLite data = new JobsSQLite(plugin, Jobs.getFolder());
	data.initialize();
	return data;
    }

    public DataBaseType getDbType() {
	return DbType;
    }

}
