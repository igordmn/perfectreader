#pragma once

#include "../util/Debug.h"

#undef FTERRORS_H_
#define FT_ERRORDEF(e, v, s)  { e, s },
#define FT_ERROR_START_LIST     {
#define FT_ERROR_END_LIST       { 0, nullptr } };

namespace dmi {
    const struct {
        int errorCode;
        const char *errorMessage;
    } ftErrors[] =

#include FT_ERRORS_H

#define FT_CHECK(errorCode) CHECKM(errorCode == 0, ftErrorMessage(errorCode))

    const char *ftErrorMessage(int errorCode) {
        for (int i = 0; i < sizeof(ftErrors); i++) {
            if (ftErrors[i].errorCode == errorCode) {
                return ftErrors[i].errorMessage;
            }
        }
        return "unknown error";
    }
}