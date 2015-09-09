#include "WebSchedulerImpl.h"

#include "TypoWebLibrary.h"
#include "WebThreadImpl.h"

using namespace blink;

namespace typo {

class WebSchedulerImpl::IdleTaskWrapper : public WebThread::Task {
private:
    WebThread::IdleTask* idleTask_;

public:
    IdleTaskWrapper(WebThread::IdleTask* idleTask) : idleTask_(idleTask) {}
    virtual ~IdleTaskWrapper() {
        delete idleTask_;
    }

    virtual void run() override {
        idleTask_->run(0);
    }
};

void WebSchedulerImpl::postIdleTask(const WebTraceLocation& location, WebThread::IdleTask* task) {
    TypoWebLibrary::mainThread()->postTask(location, new IdleTaskWrapper(task));
}

void WebSchedulerImpl::postNonNestableIdleTask(const WebTraceLocation& location, WebThread::IdleTask* task) {
    TypoWebLibrary::mainThread()->postTask(location, new IdleTaskWrapper(task));
}

void WebSchedulerImpl::postIdleTaskAfterWakeup(const WebTraceLocation& location, WebThread::IdleTask* task) {
    TypoWebLibrary::mainThread()->postTask(location, new IdleTaskWrapper(task));
}

void WebSchedulerImpl::postLoadingTask(const WebTraceLocation& location, WebThread::Task* task) {
    TypoWebLibrary::mainThread()->postTask(location, task);
}

void WebSchedulerImpl::postTimerTask(const WebTraceLocation& location, WebThread::Task* task, long long delayMs) {
    TypoWebLibrary::mainThread()->postDelayedTask(location, task, delayMs);
}

}
