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
<body class="bg-light">

<nav class="navbar navbar-expand-lg navbar-light bg-light px-3">
    <a class="navbar-brand fw-bold text-primary" href="/dashboard" style="font-size: 1.4rem; letter-spacing: 0.5px;">
        DDNS
    </a>
</nav>

<style>
    nav.navbar {
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        z-index: 10;
    }
</style>

<!-- Центрирование формы по вертикали и горизонтали -->
<div class="d-flex justify-content-center align-items-center vh-100">
    <div class="card shadow p-4" style="width: 100%; max-width: 400px;">
        <h3 class="text-center mb-4">Login</h3>

        <#if error??>
            <div class="alert alert-danger">${error}</div>
        </#if>

        <form method="post" action="/login">
            <div class="mb-3">
                <label for="username" class="form-label">Username</label>
                <input type="text" class="form-control" id="username" name="username" required autofocus>
            </div>
            <div class="mb-3">
                <label for="password" class="form-label">Password</label>
                <input type="password" class="form-control" id="password" name="password" required>
            </div>
            <div class="d-grid">
                <button type="submit" class="btn btn-primary">Log In</button>
            </div>
        </form>
    </div>
</div>

</body>
</html>
