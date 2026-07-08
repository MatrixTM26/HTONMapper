package com.htonmapper.core;

import java.util.Arrays;
import java.util.List;

public class SubdomainWordlist {

    public static List<String> GetDefaultWordlist() {
        return Arrays.asList(
                "www", "mail", "ftp", "webmail", "smtp", "pop", "ns1", "ns2", "ns3", "ns4",
                "autodiscover", "autoconfig", "m", "mobile", "api", "api2", "dev", "staging",
                "test", "demo", "beta", "admin", "administrator", "portal", "dashboard", "panel",
                "cpanel", "whm", "webdisk", "vpn", "remote", "ssh", "sftp", "git", "gitlab",
                "github", "jenkins", "ci", "cd", "docker", "registry", "k8s", "kubernetes",
                "cloud", "cdn", "static", "assets", "media", "img", "images", "video", "files",
                "download", "downloads", "upload", "uploads", "backup", "backups", "old", "new",
                "app", "apps", "web", "web1", "web2", "server", "srv", "host", "db", "database",
                "mysql", "postgres", "redis", "mongo", "elastic", "kibana", "grafana", "prometheus",
                "monitor", "monitoring", "status", "health", "metrics", "logs", "log", "analytics",
                "stats", "stat", "report", "reports", "billing", "payment", "payments", "shop",
                "store", "cart", "checkout", "blog", "news", "forum", "forums", "community",
                "support", "help", "helpdesk", "ticket", "tickets", "kb", "docs", "documentation",
                "wiki", "confluence", "jira", "internal", "intranet", "extranet", "partner",
                "partners", "client", "clients", "customer", "customers", "account", "accounts",
                "login", "signin", "signup", "register", "auth", "sso", "oauth", "identity",
                "secure", "security", "firewall", "proxy", "gateway", "lb", "loadbalancer",
                "edge", "origin", "cache", "search", "elasticsearch", "solr", "queue", "mq",
                "kafka", "rabbitmq", "worker", "workers", "cron", "scheduler", "batch", "jobs",
                "smtp2", "mail2", "exchange", "owa", "email", "mx", "chat", "im", "voip", "sip",
                "pbx", "conference", "meet", "video2", "stream", "streaming", "live", "vod",
                "test1", "test2", "sandbox", "preview", "preprod", "production", "prod", "uat",
                "qa", "release", "build", "artifacts", "repo", "repository", "package", "packages",
                "vault", "consul", "nomad", "etcd", "zookeeper", "rancher", "openshift", "helm"
        );
    }
}
