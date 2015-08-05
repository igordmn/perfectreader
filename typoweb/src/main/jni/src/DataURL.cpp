#include "DataURL.h"

#include "util/StringUtils.h"
#include "util/UriUtils.h"
#include <algorithm>
#include "third_party/modp_b64/modp_b64.h"

using namespace std;

namespace typo {

void DataURL::parse(const string& url, string* mimeType, string* charset, string* data) {
    auto afterColon = std::find(url.begin(), url.end(), ':');
    auto comma = std::find(url.begin(), url.end(), ',');

    if (afterColon == url.end() || comma == url.end()) {
        return;
    }

    bool isBase64 = false;

    // default values
    mimeType->assign("text/plain");
    charset->assign("US-ASCII");

    vector<string> metaItems = StringUtils::split(string(afterColon + 1, comma), ';');
    for (string& metaItem : metaItems) {
        if (metaItem.compare(0, 8, "charset=") == 0) {
            charset->assign(metaItem.substr(8));
        } else if (metaItem == "base64") {
            isBase64 = true;
        } else {
            mimeType->swap(metaItem);
        }
    }

    string encodedData = string(comma + 1, url.end());
    encodedData = UriUtils::uriDecode(encodedData);

    if (isBase64) {
        // remove whitespaces
        encodedData.erase(
            std::remove_if(encodedData.begin(), encodedData.end(), [](wchar_t ch) {
                return ch == ' ' || ch == '\r' || ch == '\n' || ch == '\t';
            }),
            encodedData.end()
        );
        data->swap(modp_b64_decode(encodedData));
    } else {
        data->swap(encodedData);
    }
}

}
