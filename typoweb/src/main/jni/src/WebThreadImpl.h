#pragma once

#include "util/JniUtils.h"
#include "third_party/WebKit/public/platform/WebThread.h"
#include "WebSchedulerImpl.h"

namespace typo {

class TypoWebLibrary;

class WebThreadImpl : public blink::WebThread {
public:
    static void registerJni();
    static WebThreadImpl* current();
    static void yieldCurrent();

    WebThreadImpl(const char* name);
    virtual ~WebThreadImpl();

    void cancelTask(Task* task);

    virtual void postTask(const blink::WebTraceLocation&, Task* task) override;
    virtual void postDelayedTask(const blink::WebTraceLocation&, Task* task, long long delayMs) override;
    virtual bool isCurrentThread() const override ;
    virtual blink::PlatformThreadId threadId() const override;
    virtual void addTaskObserver(TaskObserver* taskObserver) override ;
    virtual void removeTaskObserver(TaskObserver* taskObserver) override;
    virtual blink::WebScheduler* scheduler() const override;
    
    // used by java
    static void nativeRunTask(JNIEnv*, jobject, jlong nativeTask);
    static void nativeDeleteTask(JNIEnv*, jobject, jlong nativeTask);
    static void nativeWillProcessTask(JNIEnv*, jobject, jlong nativeTaskObserver);
    static void nativeDidProcessTask(JNIEnv*, jobject, jlong nativeTaskObserver);

private:
    friend TypoWebLibrary;

    jobject jobj_;
    WebSchedulerImpl* webScheduler_;
};

}
