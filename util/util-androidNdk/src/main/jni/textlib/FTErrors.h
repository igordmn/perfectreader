#pragma once

#include "../util/Debug.h"

#undef FTERRORS_H_
#define FT_ERRORDEF(e, v, s)  { e, s },
#define FT_ERROR_START_LIST     {
#define FT_ERROR_END_LIST       { 0, nullptr } };

#define FT_CHECK(errorCode) CHECKM(errorCode == 0, ftErrorMessage(errorCode))
#define FT_CHECKM(errorCode, message, ...) CHECKM(errorCode == 0, formatFTErrorMessage(errorCode, message, ## __VA_ARGS__))

namespace dmi {
    const struct {
        int errorCode;
        const char *errorMessage;
    } ftErrors[] =

#include FT_ERRORS_H

    inline const char *ftErrorMessage(int errorCode) {
        for (int i = 0; i < sizeof(ftErrors); i++) {
            if (ftErrors[i].errorCode == errorCode) {
                return ftErrors[i].errorMessage;
            }
        }
        return "unknown error";
    }

    template<typename ... Args>
    std::string formatFTErrorMessage(int errorCode, const std::string &message, Args ... args) {
        const char *errorCodeMessage = ftErrorMessage(errorCode);
        std:: string userMessage = dmi::formatErrorMessage(message, args ...);
        return formatErrorMessage("%s; %s", errorCodeMessage, userMessage.c_str());
    }
}