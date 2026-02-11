// Demo mode helper - sets a demo user/token when ?demo=1 is present
(function(){
    try {
        const url = new URL(window.location.href);
        if (url.searchParams.get('demo') === '1') {
            const demoUser = { id: 1, username: 'demo', role: 'ADMIN', balance: 10000 };
            localStorage.setItem('token', 'demo-token');
            localStorage.setItem('user', JSON.stringify(demoUser));
            console.info('Demo mode enabled: demo user injected into localStorage');
        }
    } catch (e) {
        // ignore
    }
})();
