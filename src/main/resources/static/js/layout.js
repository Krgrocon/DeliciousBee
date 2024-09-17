document.addEventListener('DOMContentLoaded', function () {
    console.log("페이지 로드 완료");

    // 페이지 로드 시 인증 상태 확인
    checkAuth();

    // OAuth2 콜백 URL 처리
    if (window.location.pathname.includes('/login/oauth2/code/google')) {
        console.log("OAuth2 콜백 URL 확인");
        // OAuth2 인증 후 서버가 쿠키를 설정하고 리디렉트하므로 추가 처리는 필요 없음
    }

    // 로그인 모달 제어
    var loginButton = document.getElementById('loginButton');
    if (loginButton) {
        loginButton.addEventListener('click', function (event) {
            event.preventDefault();
            var loginModal = document.getElementById('loginModal');
            if (loginModal) loginModal.style.display = 'block';
        });
    }

    // 로그아웃 버튼에 이벤트 리스너 추가
    var logoutButton = document.getElementById('logoutButton');
    if (logoutButton) {
        logoutButton.addEventListener('click', function(event) {
            event.preventDefault();
            logout();
        });
    }

    // 로그아웃 함수
    function logout() {
        fetch('member/logout', {
            method: 'POST',
            credentials: 'include'  // 쿠키 포함
        })
            .then(response => {
                if (response.ok) {
                    alert('로그아웃되었습니다.');
                    location.reload();  // 로그아웃 후 페이지 새로고침
                } else {
                    alert('로그아웃에 실패했습니다.');
                }
            })
            .catch(error => {
                console.error('로그아웃 오류:', error);
                alert('로그아웃 중 오류가 발생했습니다.');
            });
    }

    // 마이페이지 이동
    var myPageLink = document.getElementById('myPageLink');
    if (myPageLink) {
        myPageLink.addEventListener('click', function (event) {
            event.preventDefault();
            fetch('member/api/check-auth', {
                method: 'GET',
                credentials: 'include'  // 쿠키 포함
            })
                .then(response => response.json())
                .then(data => {
                    if (data.isAuthenticated) {
                        window.location.href = '/member/myPage';
                    } else {
                        alert('인증 실패: 다시 로그인해 주세요.');
                        window.location.href = '/member/login';
                    }
                })
                .catch(error => {
                    console.error('오류 발생:', error);
                    alert('오류 발생');
                });
        });
    }

    // 일반 로그인 처리
    var loginForm = document.querySelector('#loginModal form');
    if (loginForm) {
        loginForm.addEventListener('submit', function (event) {
            event.preventDefault();
            var formData = new FormData(loginForm);
            var loginData = {
                member_id: formData.get('member_id'),
                password: formData.get('password')
            };
            fetch('member/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(loginData),
                credentials: 'include'  // 쿠키 포함
            })
                .then(response => {
                    if (response.ok) return response.text();
                    throw new Error('로그인 실패');
                })
                .then(message => {
                    alert(message);
                    closeModal();
                    // 로그인 성공 시 인증 상태 확인
                    checkAuth();
                })
                .catch(error => {
                    console.error('로그인 오류 발생:', error);
                    alert('로그인에 실패했습니다.');
                });
        });
    }

    function closeModal() {
        var loginModal = document.getElementById('loginModal');
        if (loginModal) loginModal.style.display = 'none';
    }

    // 인증 상태 확인 함수
    function checkAuth() {
        fetch('member/api/check-auth', {
            method: 'GET',
            credentials: 'include'  // 쿠키 포함
        })
            .then(response => response.json())
            .then(data => {
                if (data.isAuthenticated) {
                    document.querySelector('.logged-in-menu').style.display = 'block';
                    document.querySelector('.logged-out-menu').style.display = 'none';
                    document.querySelector('.restaurant-write').style.display = 'block';
                    // 관리자 권한 확인
                    if (data.isAdmin) {
                        document.querySelector('.admin-page').style.display = 'block';
                    }
                } else {
                    document.querySelector('.logged-in-menu').style.display = 'none';
                    document.querySelector('.logged-out-menu').style.display = 'block';
                    document.querySelector('.restaurant-write').style.display = 'none';
                    document.querySelector('.admin-page').style.display = 'none';
                }
            })
            .catch(error => {
                console.error('인증 상태 확인 오류:', error);
                // 인증 상태를 알 수 없으므로 기본적으로 로그아웃 상태로 설정
                document.querySelector('.logged-in-menu').style.display = 'none';
                document.querySelector('.logged-out-menu').style.display = 'block';
                document.querySelector('.restaurant-write').style.display = 'none';
                document.querySelector('.admin-page').style.display = 'none';
            });
    }
});