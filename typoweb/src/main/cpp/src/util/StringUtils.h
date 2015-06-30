#pragma once

#include <string>
#include <vector>

namespace typo {

class StringUtils {
public:
    static std::vector<std::string> split(const std::string &str, char delimeter);
    static std::string toString(long long number);
};

}
