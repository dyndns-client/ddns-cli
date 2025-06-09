<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Create DDNS Profile</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <script>
        function toggleDiscoveryFields() {
            const method = document.querySelector('input[name="discoveryMethod"]:checked')?.value;
            document.querySelectorAll(".discovery-section").forEach(s => s.style.display = "none");
            const section = document.getElementById("discovery-" + method?.toLowerCase());
            if (section) section.style.display = "block";
        }

        function toggleStunServerFields() {
            const stunServer = document.getElementById("stunServer").value;
            const customStunFields = document.getElementById("customStunFields");

            if (stunServer === "custom") {
                customStunFields.style.display = "block";
                // Clear hidden fields when switching to custom
                document.getElementById("stunHost").value = "";
                document.getElementById("stunPort").value = "";
            } else {
                customStunFields.style.display = "none";
                // Parse predefined server and populate hidden fields
                const parts = stunServer.split(':');
                if (parts.length === 2) {
                    document.getElementById("stunHost").value = parts[0];
                    document.getElementById("stunPort").value = parts[1];
                }
            }
        }

        function updateStunFields() {
            const hostInput = document.getElementById("customStunHostInput");
            const portInput = document.getElementById("customStunPortInput");

            if (hostInput && portInput) {
                document.getElementById("stunHost").value = hostInput.value;
                document.getElementById("stunPort").value = portInput.value;
            }
        }

        function toggleDnsServerFields() {
            const dnsServer = document.getElementById("dnsServer").value;
            const customDnsFields = document.getElementById("customDnsFields");
            const predefinedDnsFields = document.getElementById("predefinedDnsFields");

            if (dnsServer === "custom") {
                customDnsFields.style.display = "block";
                predefinedDnsFields.style.display = "none";
                // Clear hidden fields
                document.getElementById("dnsDomain").value = "";
                document.getElementById("dnsResolver").value = "";
                document.getElementById("dnsPort").value = "";
                document.getElementById("dnsRecordType").value = "";
            } else {
                customDnsFields.style.display = "none";
                predefinedDnsFields.style.display = "block";
                // Parse predefined server (format: domain:resolver:port)
                const parts = dnsServer.split(':');
                if (parts.length >= 3) {
                    document.getElementById("predefinedDnsDomain").textContent = parts[0];
                    document.getElementById("predefinedDnsResolver").textContent = parts[1];
                    document.getElementById("predefinedDnsPort").textContent = parts[2];
                    document.getElementById("predefinedDnsRecordType").textContent = "TXT";

                    // Set hidden fields
                    document.getElementById("dnsDomain").value = parts[0];
                    document.getElementById("dnsResolver").value = parts[1];
                    document.getElementById("dnsPort").value = parts[2];
                    document.getElementById("dnsRecordType").value = "TXT";
                }
            }
        }

        function updateDnsFields() {
            const domainInput = document.getElementById("customDnsDomainInput");
            const resolverInput = document.getElementById("customDnsResolverInput");
            const portInput = document.getElementById("customDnsPortInput");
            const recordTypeInput = document.getElementById("customDnsRecordTypeInput");

            if (domainInput) document.getElementById("dnsDomain").value = domainInput.value;
            if (resolverInput) document.getElementById("dnsResolver").value = resolverInput.value;
            if (portInput) document.getElementById("dnsPort").value = portInput.value;
            if (recordTypeInput) document.getElementById("dnsRecordType").value = recordTypeInput.value;
        }

        function addRow(containerId, namePrefix) {
            const container = document.getElementById(containerId);
            const row = document.createElement("div");
            row.classList.add("row", "mb-2");

            row.innerHTML =
                '<div class="col">' +
                '<input type="text" class="form-control" placeholder="Name" name="' + namePrefix + 'Name[]">' +
                '</div>' +
                '<div class="col">' +
                '<input type="text" class="form-control" placeholder="Value" name="' + namePrefix + 'Value[]">' +
                '</div>' +
                '<div class="col-auto">' +
                '<button type="button" class="btn btn-danger" onclick="this.parentElement.parentElement.remove()">&times;</button>' +
                '</div>';

            container.appendChild(row);
        }

        function loadProviderConfig(provider) {
            const config = providerConfigs[provider];

            // Fill fixed fields
            document.getElementById('providerUrl').value = config.url;
            document.getElementById('providerMethod').value = config.method;
            document.getElementById('providerSocketTimeout').value = config.socketTimeout;
            document.getElementById('providerIpLocationType').value = config.ipLocation.type;
            document.getElementById('providerIpLocationName').value = config.ipLocation.name;

            // Enable/disable fields based on provider
            const isCustom = provider === 'Custom';
            document.getElementById('providerUrl').readOnly = !isCustom;
            document.getElementById('providerMethod').disabled = !isCustom;
            document.getElementById('providerSocketTimeout').readOnly = !isCustom;
            document.getElementById('providerIpLocationType').disabled = !isCustom;
            document.getElementById('providerIpLocationName').readOnly = !isCustom;

            // Generate user input fields
            generateUserFields(config.userFields);

            // Clear containers
            clearContainer('headersContainer');
            clearContainer('parametersContainer');

            // Setup form submission handler
            setupFormSubmission(provider);
        }

        function generateUserFields(fields) {
            const container = document.getElementById('userInputFields');
            container.innerHTML = '';

            fields.forEach(field => {
                const fieldDiv = document.createElement('div');
                fieldDiv.className = 'mb-3';

                const placeholder = field.placeholder || '';

                fieldDiv.innerHTML =
                    '<label for="temp_' + field.name + '" class="form-label">' + field.label + '</label>' +
                    '<input type="' + field.type + '" class="form-control" id="temp_' + field.name + '" ' +
                    'placeholder="' + placeholder + '" required>';

                container.appendChild(fieldDiv);
            });
        }

        function setupFormSubmission(provider) {
            const form = document.getElementById('createProfileForm');

            // Remove existing listener if any
            form.removeEventListener('submit', handleFormSubmit);

            // Add new listener
            form.addEventListener('submit', function (e) {
                handleFormSubmit(e, provider);
            });
        }

        function handleFormSubmit(event, provider) {
            // Collect user inputs and convert to parameters/headers
            const config = providerConfigs[provider];
            const userValues = {};

            config.userFields.forEach(field => {
                const input = document.getElementById('temp_' + field.name);
                if (input && input.value.trim()) {
                    userValues[field.name] = input.value.trim();
                }
            });

            // Build parameters and headers based on provider
            const parameters = {};
            const headers = {};

            if (provider === 'DYNU') {
                if (userValues.hostname) parameters.hostname = userValues.hostname;
                if (userValues.username) parameters.username = userValues.username;
                if (userValues.password) parameters.password = userValues.password;

            } else if (provider === 'NOIP') {
                if (userValues.hostname) parameters.hostname = userValues.hostname;
                if (userValues.username && userValues.password) {
                    headers.Authorization = 'Basic ' + btoa(userValues.username + ':' + userValues.password);
                }

            } else if (provider === 'DuckDNS') {
                if (userValues.domains) parameters.domains = userValues.domains;
                if (userValues.token) parameters.token = userValues.token;

            } else if (provider === 'FreeDNS') {
                if (userValues.host) parameters.host = userValues.host;
            }

            // Add manually added headers/parameters from the form
            const manualHeaders = collectManualKeyValuePairs('headersContainer');
            const manualParameters = collectManualKeyValuePairs('parametersContainer');

            // Merge with auto-generated
            Object.assign(headers, manualHeaders);
            Object.assign(parameters, manualParameters);

            // Convert to string format: key=value,key2=value2
            const headersString = Object.entries(headers)
                .map(function (entry) {
                    return entry[0] + '=' + entry[1];
                })
                .join(',');

            const parametersString = Object.entries(parameters)
                .map(function (entry) {
                    return entry[0] + '=' + entry[1];
                })
                .join(',');

            // Set hidden fields
            document.getElementById('headersData').value = headersString;
            document.getElementById('parametersData').value = parametersString;
        }

        function collectManualKeyValuePairs(containerId) {
            const container = document.getElementById(containerId);
            const result = {};

            const rows = container.querySelectorAll('.row');
            rows.forEach(function (row) {
                const inputs = row.querySelectorAll('input[type="text"]');
                if (inputs.length === 2) {
                    const key = inputs[0].value.trim();
                    const value = inputs[1].value.trim();
                    if (key && value) {
                        result[key] = value;
                    }
                }
            });

            return result;
        }

        function clearContainer(containerId) {
            document.getElementById(containerId).innerHTML = '';
        }

        function validateForm(event) {
            const name = document.getElementById("name").value.trim();
            if (name === "") {
                alert("Profile name is required.");
                event.preventDefault();
                return false;
            }

            const discoveryMethod = document.querySelector('input[name="discoveryMethod"]:checked')?.value;

            // Validate STUN fields
            if (discoveryMethod === "STUN") {
                const stunServer = document.getElementById("stunServer").value;
                if (stunServer === "custom") {
                    const customStunHost = document.getElementById("customStunHostInput").value.trim();
                    const customStunPort = document.getElementById("customStunPortInput").value.trim();
                    if (!customStunHost) {
                        alert("Custom STUN host is required.");
                        event.preventDefault();
                        return false;
                    }
                    if (!customStunPort || isNaN(customStunPort) || customStunPort < 1 || customStunPort > 65535) {
                        alert("Valid STUN port is required (1-65535).");
                        event.preventDefault();
                        return false;
                    }
                }
            }

            // Validate DNS fields
            if (discoveryMethod === "DNS") {
                const dnsServer = document.getElementById("dnsServer").value;
                if (dnsServer === "custom") {
                    const dnsDomain = document.getElementById("customDnsDomainInput").value.trim();
                    const dnsResolver = document.getElementById("customDnsResolverInput").value.trim();
                    const dnsPort = document.getElementById("customDnsPortInput").value.trim();

                    if (!dnsDomain) {
                        alert("DNS domain is required.");
                        event.preventDefault();
                        return false;
                    }
                    if (!dnsResolver) {
                        alert("DNS resolver is required.");
                        event.preventDefault();
                        return false;
                    }
                    if (!dnsPort || isNaN(dnsPort) || dnsPort < 1 || dnsPort > 65535) {
                        alert("Valid DNS port is required (1-65535).");
                        event.preventDefault();
                        return false;
                    }
                }
            }

            // Validate HTTP fields
            if (discoveryMethod === "HTTP") {
                const httpUrl = document.getElementById("httpUrl").value.trim();
                if (!httpUrl) {
                    alert("HTTP URL is required.");
                    event.preventDefault();
                    return false;
                }
            }

            return true;
        }

        // Provider configurations
        const providerConfigs = {
            DYNU: {
                url: 'https://api.dynu.com/nic/update',
                method: 'GET',
                socketTimeout: 10000,
                ipLocation: {
                    type: 'BODY',
                    name: 'myip'
                },
                userFields: [
                    {name: 'hostname', label: 'Hostname', type: 'text', placeholder: 'example.dynu.com'},
                    {name: 'username', label: 'Username', type: 'text', placeholder: ''},
                    {name: 'password', label: 'Password', type: 'password', placeholder: ''}
                ]
            },
            NOIP: {
                url: 'https://dynupdate.no-ip.com/nic/update',
                method: 'GET',
                socketTimeout: 10000,
                ipLocation: {
                    type: 'BODY',
                    name: 'myip'
                },
                userFields: [
                    {name: 'username', label: 'Username', type: 'text', placeholder: ''},
                    {name: 'password', label: 'Password', type: 'password', placeholder: ''},
                    {name: 'hostname', label: 'Hostname', type: 'text', placeholder: 'example.no-ip.org'}
                ]
            },
            DuckDNS: {
                url: 'https://www.duckdns.org/update',
                method: 'GET',
                socketTimeout: 10000,
                ipLocation: {
                    type: 'BODY',
                    name: 'ip'
                },
                userFields: [
                    {name: 'domains', label: 'Domains', type: 'text', placeholder: 'example'},
                    {name: 'token', label: 'Token', type: 'text', placeholder: ''}
                ]
            },
            FreeDNS: {
                url: 'https://freedns.afraid.org/dynamic/update.php',
                method: 'GET',
                socketTimeout: 10000,
                ipLocation: {
                    type: 'BODY',
                    name: 'address'
                },
                userFields: [
                    {name: 'username', label: 'Username', type: 'text', placeholder: ''},
                    {name: 'password', label: 'Password', type: 'password', placeholder: ''},
                    {name: 'host', label: 'Host', type: 'text', placeholder: 'example.afraid.org'}
                ]
            },
            Custom: {
                url: '',
                method: 'GET',
                socketTimeout: 10000,
                ipLocation: {
                    type: 'BODY',
                    name: 'myip'
                },
                userFields: []
            }
        };

        document.addEventListener("DOMContentLoaded", function () {
            toggleDiscoveryFields();
            toggleStunServerFields();
            toggleDnsServerFields();
            loadProviderConfig('DYNU');
            document.getElementById("createProfileForm").addEventListener("submit", validateForm);
        });
    </script>
</head>
<body class="bg-light">
<div class="container mt-5 mb-5" style="max-width: 900px;">
    <h2 class="mb-4">Create DDNS Profile</h2>
    <form method="post" action="/profile/create" id="createProfileForm">
        <!-- Basic Info -->
        <div class="card mb-4">
            <div class="card-header">General Information</div>
            <div class="card-body">
                <div class="mb-3">
                    <label for="name" class="form-label">Profile Name</label>
                    <input type="text" class="form-control" id="name" name="name" required>
                </div>
                <div class="mb-3">
                    <label class="form-label">IP Version</label>
                    <select class="form-select" name="ipVersion">
                        <option value="IPv4">IPv4</option>
                        <option value="IPv6">IPv6</option>
                    </select>
                </div>
                <div class="form-check">
                    <input class="form-check-input" type="checkbox" name="active" id="active" checked>
                    <label class="form-check-label" for="active">Active</label>
                </div>
            </div>
        </div>

        <!-- Discovery -->
        <div class="card mb-4">
            <div class="card-header">Discovery Method</div>
            <div class="card-body">
                <div class="mb-3">
                    <label class="form-label">Method</label><br>
                    <div class="form-check form-check-inline">
                        <input class="form-check-input" type="radio" name="discoveryMethod" value="STUN" checked
                               onclick="toggleDiscoveryFields()">
                        <label class="form-check-label">STUN</label>
                    </div>
                    <div class="form-check form-check-inline">
                        <input class="form-check-input" type="radio" name="discoveryMethod" value="HTTP"
                               onclick="toggleDiscoveryFields()">
                        <label class="form-check-label">HTTP</label>
                    </div>
                    <div class="form-check form-check-inline">
                        <input class="form-check-input" type="radio" name="discoveryMethod" value="DNS"
                               onclick="toggleDiscoveryFields()">
                        <label class="form-check-label">DNS</label>
                    </div>
                </div>

                <!-- STUN -->
                <div class="discovery-section" id="discovery-stun">
                    <div class="mb-3">
                        <label for="discoveryInterval" class="form-label">Discovery Interval (seconds)</label>
                        <input type="number" class="form-control" id="discoveryInterval" name="discoveryInterval"
                               value="300">
                    </div>
                    <div class="mb-3">
                        <label for="discoverySocketTimeout" class="form-label">Socket Timeout (milliseconds)</label>
                        <input type="number" class="form-control" id="discoverySocketTimeout"
                               name="discoverySocketTimeout" value="10000">
                    </div>
                    <label class="form-label">Select STUN Server</label>
                    <select class="form-select mb-2" name="stunServer" id="stunServer"
                            onchange="toggleStunServerFields()">
                        <option value="stun.l.google.com:19302">stun.l.google.com:19302</option>
                        <option value="stun1.l.google.com:19302">stun1.l.google.com:19302</option>
                        <option value="stun2.l.google.com:19302">stun2.l.google.com:19302</option>
                        <option value="stun3.l.google.com:19302">stun3.l.google.com:19302</option>
                        <option value="stun4.l.google.com:19302">stun4.l.google.com:19302</option>
                        <option value="custom">Custom</option>
                    </select>
                    <div id="customStunFields" style="display: none;">
                        <input type="text" class="form-control mb-2" id="customStunHostInput"
                               placeholder="STUN Host" oninput="updateStunFields()">
                        <input type="number" class="form-control" id="customStunPortInput"
                               placeholder="STUN Port" oninput="updateStunFields()">
                    </div>

                    <!-- Hidden fields for STUN -->
                    <input type="hidden" id="stunHost" name="stunHost" value="">
                    <input type="hidden" id="stunPort" name="stunPort" value="">
                </div>

                <!-- HTTP -->
                <div class="discovery-section" id="discovery-http" style="display:none;">
                    <div class="mb-3">
                        <label for="httpDiscoveryInterval" class="form-label">Discovery Interval (seconds)</label>
                        <input type="number" class="form-control" id="httpDiscoveryInterval" name="discoveryInterval"
                               value="300">
                    </div>
                    <div class="mb-3">
                        <label for="httpDiscoverySocketTimeout" class="form-label">Socket Timeout (milliseconds)</label>
                        <input type="number" class="form-control" id="httpDiscoverySocketTimeout"
                               name="discoverySocketTimeout" value="10000">
                    </div>
                    <input type="text" class="form-control mb-2" id="httpUrl" name="httpUrl" placeholder="HTTP URL">
                    <select class="form-select mb-2" name="httpMethod">
                        <option value="GET">GET</option>
                        <option value="POST">POST</option>
                    </select>
                    <label class="form-label mt-3">Request Headers</label>
                    <div id="httpHeadersContainer"></div>
                    <button type="button" class="btn btn-sm btn-outline-secondary mb-3"
                            onclick="addRow('httpHeadersContainer', 'httpHeader')">+ Add Header
                    </button>
                    <label class="form-label">Request Parameters</label>
                    <div id="httpParamsContainer"></div>
                    <button type="button" class="btn btn-sm btn-outline-secondary mb-3"
                            onclick="addRow('httpParamsContainer', 'httpParam')">+ Add Parameter
                    </button>
                    <select class="form-select mb-2" name="responseContentType">
                        <option value="TEXT_PLAIN">Response Type: text/plain</option>
                        <option value="APPLICATION_JSON">Response Type: application/json</option>
                    </select>
                    <select class="form-select mb-2" name="httpIpLocationType">
                        <option value="BODY">IP in body</option>
                        <option value="HEADER">IP in header</option>
                    </select>
                    <input type="text" class="form-control mb-2" name="httpIpLocationName" id="httpIpLocationName"
                           placeholder="Field/Header name for IP extraction">
                </div>

                <!-- DNS -->
                <div class="discovery-section" id="discovery-dns" style="display:none;">
                    <div class="mb-3">
                        <label for="dnsDiscoveryInterval" class="form-label">Discovery Interval (seconds)</label>
                        <input type="number" class="form-control" id="dnsDiscoveryInterval" name="discoveryInterval"
                               value="300">
                    </div>
                    <div class="mb-3">
                        <label for="dnsDiscoverySocketTimeout" class="form-label">Socket Timeout (milliseconds)</label>
                        <input type="number" class="form-control" id="dnsDiscoverySocketTimeout"
                               name="discoverySocketTimeout" value="10000">
                    </div>
                    <label class="form-label">Select DNS Server</label>
                    <select class="form-select mb-2" name="dnsServer" id="dnsServer" onchange="toggleDnsServerFields()">
                        <option value="o-o.myaddr.l.google.com:ns1.google.com:53">o-o.myaddr.l.google.com (Google)
                        </option>
                        <option value="custom">Custom</option>
                    </select>

                    <!-- Predefined DNS fields (read-only display) -->
                    <div id="predefinedDnsFields">
                        <div class="mb-3">
                            <label class="form-label">Domain:</label>
                            <div class="form-control bg-light" id="predefinedDnsDomain">o-o.myaddr.l.google.com</div>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Resolver:</label>
                            <div class="form-control bg-light" id="predefinedDnsResolver">ns1.google.com</div>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Port:</label>
                            <div class="form-control bg-light" id="predefinedDnsPort">53</div>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Record Type:</label>
                            <div class="form-control bg-light" id="predefinedDnsRecordType">TXT</div>
                        </div>
                    </div>

                    <!-- Custom DNS fields -->
                    <div id="customDnsFields" style="display:none;">
                        <input type="text" class="form-control mb-2" id="customDnsDomainInput"
                               placeholder="Domain" oninput="updateDnsFields()">
                        <input type="text" class="form-control mb-2" id="customDnsResolverInput"
                               placeholder="Resolver" oninput="updateDnsFields()">
                        <input type="number" class="form-control mb-2" id="customDnsPortInput"
                               placeholder="Port" value="53" oninput="updateDnsFields()">
                        <select class="form-select" id="customDnsRecordTypeInput" onchange="updateDnsFields()">
                            <option value="TXT">TXT</option>
                            <option value="A">A</option>
                            <option value="AAAA">AAAA</option>
                        </select>
                    </div>

                    <!-- Hidden fields for DNS -->
                    <input type="hidden" id="dnsDomain" name="dnsDomain" value="">
                    <input type="hidden" id="dnsResolver" name="dnsResolver" value="">
                    <input type="hidden" id="dnsPort" name="dnsPort" value="">
                    <input type="hidden" id="dnsRecordType" name="dnsRecordType" value="">
                </div>
            </div>
        </div>

        <!-- Provider Section -->
        <div class="card mb-4">
            <div class="card-header">DDNS Provider</div>
            <div class="card-body">
                <div class="mb-3">
                    <label class="form-label">Select Provider</label>
                    <div class="form-check">
                        <input class="form-check-input" type="radio" name="provider" value="DYNU"
                               checked onclick="loadProviderConfig('DYNU')">
                        <label class="form-check-label">DYNU (www.dynu.com)</label>
                    </div>
                    <div class="form-check">
                        <input class="form-check-input" type="radio" name="provider" value="NOIP"
                               onclick="loadProviderConfig('NOIP')">
                        <label class="form-check-label">NOIP (www.noip.com)</label>
                    </div>
                    <div class="form-check">
                        <input class="form-check-input" type="radio" name="provider" value="DuckDNS"
                               onclick="loadProviderConfig('DuckDNS')">
                        <label class="form-check-label">DuckDNS (www.duckdns.org)</label>
                    </div>
                    <div class="form-check">
                        <input class="form-check-input" type="radio" name="provider" value="FreeDNS"
                               onclick="loadProviderConfig('FreeDNS')">
                        <label class="form-check-label">FreeDNS (freedns.afraid.org)</label>
                    </div>
                    <div class="form-check">
                        <input class="form-check-input" type="radio" name="provider" value="Custom"
                               onclick="loadProviderConfig('Custom')">
                        <label class="form-check-label">Custom</label>
                    </div>
                </div>

                <!-- Unified Provider Configuration -->
                <div id="providerConfig">
                    <div class="mb-3">
                        <label for="providerUrl" class="form-label">Update URL</label>
                        <input type="text" class="form-control" id="providerUrl" name="url" readonly>
                    </div>
                    <div class="mb-3">
                        <label for="providerMethod" class="form-label">HTTP Method</label>
                        <select class="form-select" id="providerMethod" name="method" disabled>
                            <option value="GET">GET</option>
                            <option value="POST">POST</option>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label for="providerSocketTimeout" class="form-label">Socket Timeout (ms)</label>
                        <input type="number" class="form-control" id="providerSocketTimeout" name="socketTimeout"
                               value="10000" readonly>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">IP Location Type</label>
                        <select class="form-select" id="providerIpLocationType" name="ipLocationType" disabled>
                            <option value="BODY">BODY (GET-parameter or POST-application/x-www-form-urlencoded)</option>
                            <option value="HEADER">HEADER</option>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label for="providerIpLocationName" class="form-label">IP Location Name</label>
                        <input type="text" class="form-control" id="providerIpLocationName" name="ipLocationName"
                               readonly>
                    </div>

                    <!-- Dynamic User Input Fields -->
                    <div id="userInputFields"></div>

                    <!-- Headers Section -->
                    <div class="mb-3">
                        <label class="form-label">Headers</label>
                        <div id="headersContainer"></div>
                        <button type="button" class="btn btn-sm btn-outline-secondary mb-3"
                                onclick="addRow('headersContainer', 'header')">+ Add Header
                        </button>
                    </div>

                    <!-- Parameters Section -->
                    <div class="mb-3">
                        <label class="form-label">Parameters</label>
                        <div id="parametersContainer"></div>
                        <button type="button" class="btn btn-sm btn-outline-secondary"
                                onclick="addRow('parametersContainer', 'parameter')">+ Add Parameter
                        </button>
                    </div>

                    <!-- Hidden fields for unified data -->
                    <input type="hidden" id="headersData" name="headers" value="">
                    <input type="hidden" id="parametersData" name="parameters" value="">
                </div>
            </div>
        </div>

        <button type="submit" class="btn btn-success">Create Profile</button>
        <a href="/dashboard" class="btn btn-secondary ms-2">Cancel</a>
    </form>
</div>
</body>
</html>