<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Profile - ${profile.name}</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
<div class="container mt-4">

    <!-- Header -->
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2>Profile: ${profile.name}</h2>
        <a href="/dashboard" class="btn btn-secondary">← Back</a>
    </div>

    <!-- General Info -->
    <div class="card mb-4">
        <div class="card-header">General Info</div>
        <div class="card-body">
            <p><strong>Active:</strong> ${profile.active?string("Yes", "No")}</p>
            <p><strong>IP Version:</strong> ${profile.ipVersion}</p>
            <p><strong>Last Updated IP:</strong> ${profile.lastUpdateIp?if_exists!}</p>
            <p><strong>Discovery Method:</strong> ${profile.discoveryMethod}</p>
        </div>
    </div>

    <!-- Update Info -->
    <#if profile.updateInfo??>
        <div class="card mb-4">
            <div class="card-header">Dynamic DNS Update Info</div>
            <div class="card-body">
                <p><strong>Update URL:</strong> ${profile.updateInfo.url}</p>
                <p><strong>Method:</strong> ${profile.updateInfo.method}</p>
                <p><strong>Socket Timeout:</strong> ${profile.updateInfo.socketTimeout} ms</p>

                <#if profile.updateInfo.ipLocation??>
                    <p><strong>IP Location:</strong> ${profile.updateInfo.ipLocation.locationType}
                        <#if profile.updateInfo.ipLocation.locationName??> - ${profile.updateInfo.ipLocation.locationName}</#if>
                    </p>
                </#if>

                <#if profile.updateInfo.headers?? && profile.updateInfo.headers?size gt 0>
                    <p><strong>Headers:</strong></p>
                    <ul>
                        <#list profile.updateInfo.headers?keys as key>
                            <li>${key}: ${profile.updateInfo.headers[key]}</li>
                        </#list>
                    </ul>
                </#if>

                <#if profile.updateInfo.parameters?? && profile.updateInfo.parameters?size gt 0>
                    <p><strong>Parameters:</strong></p>
                    <ul>
                        <#list profile.updateInfo.parameters?keys as key>
                            <li>${key}: ${profile.updateInfo.parameters[key]}</li>
                        </#list>
                    </ul>
                </#if>
            </div>
        </div>
    </#if>

    <!-- Discovery Info -->
    <#if profile.discoveryMethod == "STUN" && profile.stunDiscoveryInfo??>
        <#assign info = profile.stunDiscoveryInfo>
        <#assign server = info.server>
        <#assign type = "STUN Server">
    <#elseif profile.discoveryMethod == "HTTP" && profile.httpDiscoveryInfo??>
        <#assign info = profile.httpDiscoveryInfo>
        <#assign server = info.server>
        <#assign type = "HTTP Server">
    <#elseif profile.discoveryMethod == "DNS" && profile.dnsDiscoveryInfo??>
        <#assign info = profile.dnsDiscoveryInfo>
        <#assign server = info.server>
        <#assign type = "DNS Server">
    </#if>

    <#if info?? && server??>
        <div class="card mb-4">
            <div class="card-header">Discovery Info (${type})</div>
            <div class="card-body">
                <p><strong>Discovery Interval:</strong> ${info.discoveryInterval} seconds</p>
                <p><strong>Socket Timeout:</strong> ${info.socketTimeout} ms</p>

                <#if profile.discoveryMethod == "STUN">
                    <p><strong>Host:</strong> ${server.host}</p>
                    <p><strong>Port:</strong> ${server.port?c}</p>

                <#elseif profile.discoveryMethod == "HTTP">
                    <p><strong>URL:</strong> ${server.url}</p>
                    <p><strong>Method:</strong> ${server.requestInfo.method}</p>

                    <#if server.requestInfo.headers?? && server.requestInfo.headers?size gt 0>
                        <p><strong>Request Headers:</strong></p>
                        <ul>
                            <#list server.requestInfo.headers?keys as key>
                                <li>${key}: ${server.requestInfo.headers[key]}</li>
                            </#list>
                        </ul>
                    </#if>

                    <#if server.requestInfo.parameters?? && server.requestInfo.parameters?size gt 0>
                        <p><strong>Request Parameters:</strong></p>
                        <ul>
                            <#list server.requestInfo.parameters?keys as key>
                                <li>${key}: ${server.requestInfo.parameters[key]}</li>
                            </#list>
                        </ul>
                    </#if>

                    <#if server.responseInfo??>
                        <#if server.responseInfo.contentType??>
                            <p><strong>Response Type:</strong> ${server.responseInfo.contentType.httpValue}</p>
                        </#if>

                        <#if server.responseInfo.ipLocation??>
                            <p><strong>IP Location:</strong> ${server.responseInfo.ipLocation.locationType}
                                <#if server.responseInfo.ipLocation.locationName??> - ${server.responseInfo.ipLocation.locationName}</#if>
                            </p>
                        </#if>
                    </#if>

                <#elseif profile.discoveryMethod == "DNS">
                    <p><strong>Domain Name:</strong> ${server.domainName}</p>
                    <p><strong>Resolver:</strong> ${server.dnsResolverName}</p>
                    <p><strong>Record Type:</strong> ${server.dnsRecordType}</p>
                    <p><strong>Port:</strong> ${server.port}</p>
                </#if>
            </div>
        </div>
    </#if>

    <!-- Logs -->
    <div class="card mb-4">
        <div class="card-header d-flex justify-content-between align-items-center">
            <span>Profile Logs</span>
            <div>
                <button id="autoRefreshBtn" class="btn btn-sm btn-outline-primary me-2" onclick="toggleAutoRefresh()">
                    Auto-refresh: ON
                </button>
                <button id="toggleLogsBtn" class="btn btn-sm btn-outline-secondary" onclick="toggleLogs()">
                    Collapse
                </button>
            </div>
        </div>
        <div class="card-body p-0">
            <div id="logsContainer" class="overflow-auto"
                 style="max-height: 300px; font-family: monospace; font-size: 0.9rem;">
                <#if logs?? && logs?size gt 0>
                    <#list logs as log>
                        <div class="log-entry">${log?html}</div>
                    </#list>
                <#else>
                    <div class="text-muted text-center py-3" id="noLogsMessage">No logs available</div>
                </#if>
            </div>
        </div>
    </div>
</div>

<script>
    // Получаем имя профиля из заголовка
    const profileName = "${profile.name}";
    let autoRefreshInterval = null;
    let autoRefreshEnabled = true;

    // Запускаем автообновление при загрузке страницы
    document.addEventListener('DOMContentLoaded', function () {
        startAutoRefresh();
    });

    function startAutoRefresh() {
        // Сначала очищаем любой существующий интервал
        if (autoRefreshInterval) {
            clearInterval(autoRefreshInterval);
        }

        // Устанавливаем новый интервал
        autoRefreshInterval = setInterval(fetchLogs, 1000);
        autoRefreshEnabled = true;
        document.getElementById('autoRefreshBtn').innerText = 'Auto-refresh: ON';
        document.getElementById('autoRefreshBtn').classList.remove('btn-outline-secondary');
        document.getElementById('autoRefreshBtn').classList.add('btn-outline-primary');
    }

    function stopAutoRefresh() {
        if (autoRefreshInterval) {
            clearInterval(autoRefreshInterval);
            autoRefreshInterval = null;
        }
        autoRefreshEnabled = false;
        document.getElementById('autoRefreshBtn').innerText = 'Auto-refresh: OFF';
        document.getElementById('autoRefreshBtn').classList.remove('btn-outline-primary');
        document.getElementById('autoRefreshBtn').classList.add('btn-outline-secondary');
    }

    function toggleAutoRefresh() {
        if (autoRefreshEnabled) {
            stopAutoRefresh();
        } else {
            startAutoRefresh();
        }
    }

    function fetchLogs() {
        fetch('/profile/logs?profileName=' + encodeURIComponent(profileName))
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(logs => {
                updateLogsDisplay(logs);
            })
            .catch(error => {
                console.error('Error fetching logs:', error);
                // Останавливаем автообновление при проблемах с запросом
                if (autoRefreshEnabled) {
                    stopAutoRefresh();
                }
            });
    }

    function updateLogsDisplay(logs) {
        const logsContainer = document.getElementById('logsContainer');
        const noLogsMessage = document.getElementById('noLogsMessage');

        // Очищаем текущие логи
        logsContainer.innerHTML = '';

        if (logs && logs.length > 0) {
            // У нас есть логи, отображаем их
            logs.forEach(log => {
                const logEntry = document.createElement('div');
                logEntry.className = 'log-entry';
                logEntry.textContent = log;
                logsContainer.appendChild(logEntry);
            });
        } else {
            // Нет логов
            const noLogsMsg = document.createElement('div');
            noLogsMsg.id = 'noLogsMessage';
            noLogsMsg.className = 'text-muted text-center py-3';
            noLogsMsg.textContent = 'No logs available';
            logsContainer.appendChild(noLogsMsg);
        }
    }

    function toggleLogs() {
        const logsContainer = document.getElementById('logsContainer');
        const btn = document.getElementById('toggleLogsBtn');

        if (logsContainer.style.maxHeight === '0px') {
            logsContainer.style.maxHeight = '300px';
            btn.innerText = 'Collapse';
        } else {
            logsContainer.style.maxHeight = '0px';
            btn.innerText = 'Expand';
        }
    }
</script>

<style>
    .log-entry {
        padding: 3px 8px;
        margin: 0;
        border-bottom: 1px solid #f0f0f0;
        white-space: pre;
        word-break: break-all;
        line-height: 1.2;
    }

    .log-entry:last-child {
        border-bottom: none;
    }

    #logsContainer {
        transition: max-height 0.3s ease-in-out;
    }
</style>
</body>
</html>