<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Edit Email Settings</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <script>
        function toggleExclusive(id) {
            if (id === 'ssl') {
                document.getElementById('ssl').checked = true;
                document.getElementById('starttls').checked = false;
            } else if (id === 'starttls') {
                document.getElementById('starttls').checked = true;
                document.getElementById('ssl').checked = false;
            }
        }
    </script>
</head>
<body class="bg-light">
<div class="container mt-5 mb-4" style="max-width: 600px;">
    <h2 class="mb-4">Edit Email Configuration</h2>

    <form method="post" action="/email/edit">
        <div class="mb-3">
            <label for="from" class="form-label">From</label>
            <input type="email" class="form-control" id="from" name="from"
                   value="<#if email??>${email.from!}</#if>" required>
        </div>
        <div class="mb-3">
            <label for="to" class="form-label">To</label>
            <input type="email" class="form-control" id="to" name="to"
                   value="<#if email??>${email.to!}</#if>" required>
        </div>

        <div class="mb-3">
            <label class="form-label">Encryption</label>
            <div class="form-check">
                <input class="form-check-input" type="checkbox" name="useSsl" id="ssl" value="y"
                       onclick="toggleExclusive('ssl')"
                        <#if (email?? && email.smtpProps?? && email.smtpProps["mail.smtp.ssl.enable"]?? && email.smtpProps["mail.smtp.ssl.enable"] == "true") || !(email??)>
                checked
                        </#if>>
                <label class="form-check-label" for="ssl">Use SSL</label>
            </div>
            <div class="form-check">
                <input class="form-check-input" type="checkbox" name="useStarttls" id="starttls" value="y"
                       onclick="toggleExclusive('starttls')"
                       <#if email?? && email.smtpProps?? && email.smtpProps["mail.smtp.starttls.enable"]?? && email.smtpProps["mail.smtp.starttls.enable"] == "true">checked</#if>>
                <label class="form-check-label" for="starttls">Use STARTTLS</label>
            </div>
        </div>

        <div class="mb-3">
            <label for="smtpHost" class="form-label">SMTP Host</label>
            <input type="text" class="form-control" id="smtpHost" name="smtpHost"
                   value="<#if email?? && email.smtpProps??>${email.smtpProps["mail.smtp.host"]!}</#if>" required>
        </div>
        <div class="mb-3">
            <label for="smtpPort" class="form-label">SMTP Port</label>
            <input type="text" class="form-control" id="smtpPort" name="smtpPort"
                   value="<#if email?? && email.smtpProps??>${email.smtpProps["mail.smtp.port"]!}</#if>" required>
        </div>
        <div class="mb-3">
            <label for="password" class="form-label">SMTP (Application) Password</label>
            <input type="password" class="form-control" id="password" name="password"
                   value="<#if email??>${email.password!}</#if>" required>
        </div>
        <div class="mb-3">
            <label for="subject" class="form-label">Subject</label>
            <input type="text" class="form-control" id="subject" name="subject"
                   value="<#if email??>${email.subject!}</#if>" required>
        </div>

        <div class="mt-4">
            <button type="submit" class="btn btn-primary">Save</button>
            <a href="/dashboard" class="btn btn-secondary ms-2">Cancel</a>
        </div>
    </form>
</div>
</body>
</html>
