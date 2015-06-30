function safeCall(func) {
    try {
        func();
    } catch (e) {
        console.error(e);
    }
}