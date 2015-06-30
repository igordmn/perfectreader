#pragma once

#include <string>

namespace typo {

class DataURL {
public:
    static void parse(const std::string& url, std::string* mimeType, std::string* charset, std::string* data);
};

}
