#pragma once

#include "third_party/WebKit/public/platform/WebScheduler.h"

namespace typo {

class WebSchedulerImpl : public blink::WebScheduler {
public:
    virtual void postIdleTask(const blink::WebTraceLocation&, blink::WebThread::IdleTask*) override;
    virtual void postNonNestableIdleTask(const blink::WebTraceLocation&, blink::WebThread::IdleTask*) override;
    virtual void postIdleTaskAfterWakeup(const blink::WebTraceLocation&, blink::WebThread::IdleTask*) override;
    virtual void postLoadingTask(const blink::WebTraceLocation&, blink::WebThread::Task*) override;
    virtual void postTimerTask(const blink::WebTraceLocation&, blink::WebThread::Task*, long long delayMs) override;

private:
    class IdleTaskWrapper;
};

}
