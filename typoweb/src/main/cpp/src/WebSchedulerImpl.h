#pragma once

#include "third_party/WebKit/public/platform/WebScheduler.h"

namespace typo {

class WebSchedulerImpl : public blink::WebScheduler {
public:
    virtual void postIdleTask(const blink::WebTraceLocation& location, IdleTask* task) override;
    virtual void postLoadingTask(const blink::WebTraceLocation& location, blink::WebThread::Task* task) override;

private:
    class IdleTaskWrapper;
};

}
