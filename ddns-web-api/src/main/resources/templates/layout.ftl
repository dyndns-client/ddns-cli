<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>${title?default("DDNS")}</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .profile-item {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 0.5rem 0;
            border-bottom: 1px solid #eaeaea;
        }
    </style>
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-light bg-light px-3">
    <a class="navbar-brand fw-bold text-primary" href="/dashboard" style="font-size: 1.4rem; letter-spacing: 0.5px;">
        DDNS
    </a>
    <div class="ms-auto">
        <a href="/logout" class="btn btn-outline-danger">Logout</a>
    </div>
</nav>

<div class="container mt-4">
    <#-- Вставка дочернего шаблона -->
    <@body/>
</div>

</body>
</html>