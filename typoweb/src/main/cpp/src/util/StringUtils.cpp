#include "StringUtils.h"

#include <sstream>

using std::string;
using std::vector;
using std::stringstream ;

namespace typo {

vector<string> StringUtils::split(const string &str, char delimeter) {
    vector<string> items;
    stringstream stream(str);
    string item;
    while (std::getline(stream, item, delimeter)) {
        items.push_back(item);
    }
    return items;
}

std::string StringUtils::toString(long long number) {
    std::stringstream strstream;
    strstream << number;
    return strstream.str();
}

}
