<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Dashboard</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <script>
        // Проверяем URL параметры
        const urlParams = new URLSearchParams(window.location.search);
        const message = urlParams.get('message');
        const error = urlParams.get('error');

        if (message) {
            alert(message);
        }
        if (error) {
            alert('Error: ' + error);
        }

        // Функция для удаления профиля через AJAX
        function removeProfile(profileName, element) {
            // Запрашиваем подтверждение
            if (!confirm('Are you sure you want to delete profile ' + profileName + '?')) {
                return false;
            }

            // Отправляем AJAX-запрос
            fetch('/profile/remove?profileName=' + encodeURIComponent(profileName), {
                method: 'GET'
            })
                .then(response => {
                    if (response.status === 200) {
                        // Успешное удаление - удаляем элемент из DOM
                        const listItem = element.closest('li');
                        listItem.remove();

                        // Проверяем, остались ли еще профили
                        const profilesList = document.querySelector('.list-group');
                        if (profilesList.children.length === 0) {
                            // Если профилей больше нет, показываем соответствующее сообщение
                            const profilesContainer = document.querySelector('.card-body');
                            profilesContainer.innerHTML = '<p class="text-muted">No profiles found.</p>';
                        }
                    } else {
                        alert('Failed to delete profile: ' + profileName);
                    }
                })
                .catch(error => {
                    console.error('Error deleting profile:', error);
                    alert('Error deleting profile: ' + error.message);
                });

            return false; // Предотвращаем стандартный переход по ссылке
        }

        // Функция для переключения видимости пароля
        function togglePassword() {
            const passwordSpan = document.getElementById('password-span');
            const eyeIcon = document.getElementById('eye-icon');

            if (passwordSpan.dataset.visible === 'false') {
                // Показать пароль
                passwordSpan.textContent = passwordSpan.dataset.password;
                passwordSpan.dataset.visible = 'true';
                eyeIcon.textContent = '👁️';
            } else {
                // Скрыть пароль
                const passwordLength = passwordSpan.dataset.password.length;
                passwordSpan.innerHTML = '●'.repeat(passwordLength);
                passwordSpan.dataset.visible = 'false';
                eyeIcon.textContent = '👁️';
            }
        }
    </script>
</head>
<body>

<div class="container mt-4">
    <h2 class="mb-4">Dashboard</h2>

    <!-- Email Info Block -->
    <div class="card mb-4">
        <div class="card-header d-flex justify-content-between align-items-center">
        <span>
            Email Information
            <span class="ms-1 text-muted" title="Send email when address is updated"
                  style="cursor: help;">&#9432;</span>
        </span>
            <a href="/email/edit" class="btn btn-sm btn-outline-primary">Edit</a>
        </div>
        <div class="card-body">
            <#if email??>
                <p><strong>From:</strong> ${email.from}</p>
                <p><strong>To:</strong> ${email.to}</p>
                <p><strong>Subject:</strong> ${email.subject}</p>
                <p><strong>Password:</strong>
                    <#if email.password??>
                        <span id="password-span" data-password="${email.password}" data-visible="false">
                            <#list 1..email.password?length as i>●</#list>
                        </span>
                        <span id="eye-icon" onclick="togglePassword()" style="cursor: pointer; margin-left: 5px;">👁️</span>
                    <#else>
                        <span class="text-muted">Not set</span>
                    </#if>
                </p>
                <p><strong>SMTP Properties:</strong></p>
                <ul>
                    <#list email.smtpProps?keys as key>
                        <li><strong>${key}:</strong> ${email.smtpProps[key]}</li>
                    </#list>
                </ul>
            <#else>
                <p class="text-muted">No email information configured.</p>
            </#if>
        </div>
    </div>


    <!-- Profile List -->
    <div class="card">
        <div class="card-header d-flex justify-content-between align-items-center">
            <span>Profiles</span>
            <a href="/profile/create" class="btn btn-sm btn-outline-primary">New</a>
        </div>
        <div class="card-body">
            <#if profiles?? && profiles?size gt 0>
                <ul class="list-group">
                    <#list profiles as profile>
                        <li class="list-group-item d-flex justify-content-between align-items-center">
                            <a href="/profile/show?profileName=${profile.name}">${profile.name}</a>
                            <a href="#" class="text-danger" title="Delete"
                               onclick="return removeProfile('${profile.name}', this);">
                                🗑
                            </a>
                        </li>
                    </#list>
                </ul>
            <#else>
                <p class="text-muted">No profiles found.</p>
            </#if>
        </div>
    </div>
</div>

</body>
</html>