#include "config.h"
#include <algorithm>
#include <math.h>
#include <queue>

#include <type_traits>
#include "core/typo/Words.h"
#include "base/logging.h"
#include <sys/time.h>
#include <cwchar>
#include "/home/igor/dev/chromium/src/third_party/WebKit/Source/core/typo/WordHyphenator.h"

namespace blink {

using namespace std;

WordHyphenator* hyphenator = 0;

void buildHyphenator() {
    WordHyphenator::Builder hyphenatorBuilder;
    for (const char16_t* pattern : patterns) {
        hyphenatorBuilder.addPattern(WTF::String((const UChar*) pattern));
    }
    hyphenator = hyphenatorBuilder.build();
}

void testHyphenate() {
    WordHyphenator::Context context;
    for (const char16_t* word : words) {
        hyphenator->hyphenateWord(context, (const UChar*) word, 0, char_traits<char16_t>::length(word));
    }
}

void TEST_TEST_TEST_HYPHENATE() {
    struct timespec ts;

    long long t1, t2, tsum;

    tsum = 0;
    for (int i = 0; i < 30; i++) {
        if (hyphenator) {
            delete hyphenator;
        }
        CHECK(clock_gettime(CLOCK_MONOTONIC, &ts) == 0);
        long long tt1 = ts.tv_sec * 1.0E3 + ts.tv_nsec / 1.0E6;
        buildHyphenator();
        CHECK(clock_gettime(CLOCK_MONOTONIC, &ts) == 0);
        long long tt2 = ts.tv_sec * 1.0E3 + ts.tv_nsec / 1.0E6;
        tsum += (tt2 - tt1);
    }

    LOG(WARNING) << "QQQQQ C++ BUILD " << tsum / 30;


    WordHyphenator::Context context;
    WordHyphenator::Hyphens hyphens = hyphenator->hyphenateWord(context, (const UChar*) u"нашел", 0, 5);
    if (hyphens.canHyphenate(0) != 0 || hyphens.canHyphenate(1) != 1 || hyphens.canHyphenate(2) != 0) {
        LOG(WARNING) << "QQQQQ ERROR!!!";
    }
    hyphens = hyphenator->hyphenateWord(context, (const UChar*) u"некоторое", 0, 9);
    if (hyphens.canHyphenate(0) != 0 || hyphens.canHyphenate(1) != 0 || hyphens.canHyphenate(2) != 0 || hyphens.canHyphenate(3) != 1 ||
            hyphens.canHyphenate(4) != 0 || hyphens.canHyphenate(5) != 1 || hyphens.canHyphenate(6) != 0 || hyphens.canHyphenate(7) != 1) {
        LOG(WARNING) << "QQQQQ ERROR!!!";
    }

    CHECK(clock_gettime(CLOCK_MONOTONIC, &ts) == 0);
    t1 = ts.tv_sec * 1.0E3 + ts.tv_nsec / 1.0E6;

    for (int i = 0; i < 30; i++) {
        testHyphenate();
    }

    CHECK(clock_gettime(CLOCK_MONOTONIC, &ts) == 0);
    t2 = ts.tv_sec * 1.0E3 + ts.tv_nsec / 1.0E6;

    LOG(WARNING) << "QQQQQ C++ HYPHENATE " << (t2 - t1) / 30;
}


/* ------------------------------------------ TypoHyphenator ------------------------------------------ */

WordHyphenator::WordHyphenator(Alphabet* alphabet, PatternNode* root) :
        alphabet_(alphabet), root_(root) {
}

WordHyphenator::~WordHyphenator() {
    delete alphabet_;
    delete root_;
}

bool WordHyphenator::alphabetContains(UChar ch) const {
    return alphabet_->contains(ch);
}

const WordHyphenator::Hyphens& WordHyphenator::hyphenateWord(
        Context& context,
        const UChar* word, unsigned int start, unsigned int end) const {
    unsigned int length = end >= start ? end - start : 0;
    context.upperCaseChars_.clear();
    context.originalIndices_.clear();
    context.wordLevels_.reset(length);
    context.hyphens_.resize(length);
    toUpperCase(word, start, end, context.upperCaseChars_, context.originalIndices_);
    computeWordLevels(context.upperCaseChars_, context.wordLevels_);
    createHyphens(context.wordLevels_, context.originalIndices_, context.hyphens_);
    return context.hyphens_;
}

void WordHyphenator::toUpperCase(
        const UChar* word, unsigned int start, unsigned int end,
        /* out */ vector<UChar>& upperCaseChars,
        /* out */ vector<unsigned int>& originalIndices) const {
    for (unsigned int i = start; i < end; i++) {
        UChar ch = word[i];
        const UChar* upperChars = alphabet_->upper(ch);
        if (upperChars) {
            u_int8_t upperCount = alphabet_->upperCount(ch);
            for (unsigned int j = 0; j < upperCount; j++) {
                upperCaseChars.push_back(upperChars[j]);
                originalIndices.push_back(i);
            }
        } else {
            upperCaseChars.push_back(ch);
            originalIndices.push_back(i);
        }
    }
}

void WordHyphenator::computeWordLevels(
        const vector<UChar>& upperCaseChars,
        /* out */ HyphenationLevels& wordLevels) const {
    const PatternNode* node = root_;
    int size = (int) upperCaseChars.size();
    for (int i = -1; i < size + 1; i++) {
        bool isEdge = i == -1 || i == size;
        node = node->nextNode(isEdge ? EDGE_OF_WORD : upperCaseChars[i]);
        const HyphenationLevels* levels = node->levels();
        if (levels) {
            HyphenationLevels::applyAtEndWordIndex(wordLevels, *levels, i + 1);
        }
    }
}
void WordHyphenator::createHyphens(
        const HyphenationLevels& wordLevels, vector<unsigned int>& originalIndices,
        /* out */ Hyphens& hyphens) const {
    if (hyphens.canHyphenate_.size() > 0) {
        for (unsigned int i = 0; i < hyphens.canHyphenate_.size() - 1; i++) {
            hyphens.canHyphenate_[originalIndices[i]] = wordLevels.isOdd(i);
        }
        /* не добавляем переносы в конце */
        hyphens.canHyphenate_[hyphens.canHyphenate_.size() - 1] = false;
    }
}


/* ------------------------------------------ TypoHyphenator::HyphenationLevels ------------------------------------------ */

WordHyphenator::HyphenationLevels::HyphenationLevels(u_int8_t* values, unsigned int length, unsigned int capacity) :
        values_(values), length_(length), capacity_(capacity) {
}

WordHyphenator::HyphenationLevels::HyphenationLevels() {
}

WordHyphenator::HyphenationLevels::~HyphenationLevels() {
    if (values_) {
        delete[] values_;
    }
}

void WordHyphenator::HyphenationLevels::reserve(unsigned int capacity) {
    if (capacity_ < capacity) {
        capacity_ = capacity * 2;
        if (values_) {
            delete[] values_;
        }
        values_ = new u_int8_t[capacity_];
    }
}

void WordHyphenator::HyphenationLevels::reset(unsigned int wordLength) {
    reserve(wordLength);
    length_ = wordIndexToLevelIndex(wordLength);
    fill_n(values_, length_, 0);
}

/**
 *            а л г о р и т м       (word)
 *           0 0 0 0 0 0 0 0 0      (levels)
 *
 *          . -> 0                  (apply pattern 1.1 on end 0)
 *         1 2                      (pattern levels)
 *           2 0 0 0 0 0 0 0 0      (result word levels after pattern apply)
 *
 *          . a л г -> 3
 *         1 2 3 3 2
 *           2 3 3 2 0 0 0 0 0
 *
 *            а л -> 2
 *           1 2 3
 *           1 2 3 0 0 0 0 0 0
 *
 *                        т м -> 8
 *                       1 3 2
 *           0 0 0 0 0 0 1 3 2
 *
 *                        т м . -> 9
 *                       1 4 2 5
 *           0 0 0 0 0 0 1 4 2
 *
 *                  о р -> 4
 *                 2 5 1
 *           0 0 0 2 5 1 0 0 0
 *
 *      с у п е р а л г о р и т м -> 10
 *     1 1 1 1 1 1 1 1 1 1 1 1 1 1
 *           1 1 1 1 1 1 1 1 1
 */
void WordHyphenator::HyphenationLevels::applyAtEndWordIndex(HyphenationLevels& destination, const HyphenationLevels& source, unsigned int endWordIndex) {
    applyAtEndIndex(destination, source, wordIndexToLevelIndex(endWordIndex));
}

void WordHyphenator::HyphenationLevels::applyAtEnd(HyphenationLevels& destination, const HyphenationLevels& source) {
    applyAtEndIndex(destination, source, destination.length_);
}

void WordHyphenator::HyphenationLevels::applyAtEndIndex(HyphenationLevels& destination, const HyphenationLevels& source, unsigned int endIndex) {
    int beginIndex = endIndex - source.length_;
    unsigned int destBegin = max(0, beginIndex);
    unsigned int destEnd = min(destination.length_, endIndex);
    unsigned int sourceBegin = destBegin - beginIndex;
    for (unsigned int i = destBegin, k = sourceBegin; i < destEnd; ++i, ++k) {
        destination.values_[i] = max(destination.values_[i], source.values_[k]);
    }
}

WordHyphenator::HyphenationLevels* WordHyphenator::HyphenationLevels::create(vector<u_int8_t>& values) {
    u_int8_t* levelValues = new u_int8_t[values.size()];
    copy(values.begin(), values.end(), levelValues);
    return new HyphenationLevels(levelValues, values.size(), values.size());
}

bool WordHyphenator::HyphenationLevels::isOdd(unsigned int wordIndex) const {
    unsigned int levelIndex = wordIndexToLevelIndex(wordIndex);
    u_int8_t level = levelIndex >= 0 && levelIndex < length_ ? values_[levelIndex] : 0;
    return level % 2 != 0;
}

unsigned int WordHyphenator::HyphenationLevels::wordIndexToLevelIndex(unsigned int wordIndex) {
    return wordIndex + 1;
}


/* ------------------------------------------ TypoHyphenator::PatternAlphabet ------------------------------------------ */

WordHyphenator::Alphabet::Alphabet() {
    fill_n(lowerToUpper_, MAX_CHAR, nullptr);
    fill_n(lowerToUpperCount_, MAX_CHAR, 0);
    fill_n(chars_, MAX_CHAR, false);
}

WordHyphenator::Alphabet::~Alphabet() {
    for (unsigned int i = 0; i < MAX_CHAR; i++) {
        UChar* upperChars = lowerToUpper_[i];
        if (upperChars) {
            delete[] upperChars;
        }
    }
}

void WordHyphenator::Alphabet::addChar(UChar lowerChar) {
    if (!contains(lowerChar)) {
        UChar* upperChars = 0;
        unsigned int upperCharCount = 0;
        getUpperChars(lowerChar, upperChars, upperCharCount);

        chars_[lowerChar] = true;
        lowerToUpper_[lowerChar] = upperChars;
        lowerToUpperCount_[lowerChar] = upperCharCount;
        for (unsigned int i = 0; i < upperCharCount; i++) {
            chars_[upperChars[i]] = true;
        }
    }
}

void WordHyphenator::Alphabet::getUpperChars(UChar lowerChar, /* out */ UChar*& upperChars, /* out */ unsigned int& upperCharCount) {
    bool error;
    upperCharCount = WTF::Unicode::toUpper(0, 0, &lowerChar, 1, &error);
    if (upperCharCount > 0) {
        upperChars = new UChar[upperCharCount];
        WTF::Unicode::toUpper(upperChars, upperCharCount, &lowerChar, 1, &error);
        if (error) {
            delete upperChars;
            upperChars = 0;
            upperCharCount = 0;
        }
    }
}

bool WordHyphenator::Alphabet::contains(UChar ch) const {
    return chars_[ch];
}

const UChar* WordHyphenator::Alphabet::upper(UChar ch) const {
    return lowerToUpper_[ch];
}

u_int8_t WordHyphenator::Alphabet::upperCount(UChar ch) const {
    return lowerToUpperCount_[ch];
}


/* ------------------------------------------ TypoHyphenator::PatternTreeMap ------------------------------------------ */

WordHyphenator::PatternNodeCollection::PatternNodeCollection() {
    fill_n(items_, INITIAL_CAPACITY, nullptr);
    fill_n(bucketEnds_, BUCKET_COUNT, 0);
}

WordHyphenator::PatternNodeCollection::~PatternNodeCollection() {
    delete[] items_;
}

WordHyphenator::PatternNode* WordHyphenator::PatternNodeCollection::getByChar(UChar ch) const {
    unsigned int bucket = ch % BUCKET_COUNT;
    u_int16_t begin = bucket != 0 ? bucketEnds_[bucket - 1] : 0;
    u_int16_t end = bucketEnds_[bucket];

    for (int i = begin; i < end; i++) {
        PatternNode* item = items_[i];
        if (item->ch() == ch) {
            return item;
        }
    }
    return 0;
}

void WordHyphenator::PatternNodeCollection::add(WordHyphenator::PatternNode* value) {
    if (size_ + 1 > capacity_) {
        setCapacity(size_ * 2);
    }
    items_[size_++] = value;

    sort(items_, items_ + size_, [] (const PatternNode* lhs, const PatternNode* rhs) {
        unsigned int lbucket = lhs->ch() % BUCKET_COUNT;
        unsigned int rbucket = rhs->ch() % BUCKET_COUNT;
        return lbucket != rbucket ? lbucket < rbucket : lhs->ch() < rhs->ch();
    });

    unsigned int previewBucket = 0;
    for (int i = 0; i <= size_; i++) {
        unsigned int bucket = i == size_ ? BUCKET_COUNT : items_[i]->ch() % BUCKET_COUNT;
        for (unsigned int k = previewBucket; k < bucket; k++) {
            bucketEnds_[k] = i;
        }
        previewBucket = bucket;
    }
}

void WordHyphenator::PatternNodeCollection::shrinkToFit() {
    setCapacity(size_);
}

void WordHyphenator::PatternNodeCollection::setCapacity(unsigned int capacity) {
    if (capacity >= size_) {
        PatternNode** newItems = new PatternNode*[capacity];
        copy(items_, items_ + size_, newItems);
        delete[] items_;
        items_ = newItems;
    }
}


/* ------------------------------------------ TypoHyphenator::PatternTree ------------------------------------------ */

WordHyphenator::PatternNode::PatternNode(UChar ch) :
        char_(ch){
}

WordHyphenator::PatternNode::~PatternNode() {
    for (unsigned int i = 0; i < childCount(); i++) {
        delete childAt(i);
    }
    if (ownLevels_) {
        delete levels_;
    }
}

void WordHyphenator::PatternNode::addChild(PatternNode* node) {
    children_.add(node);
}

WordHyphenator::PatternNode* WordHyphenator::PatternNode::childByChar(UChar ch) const  {
    return children_.getByChar(ch);
}

void WordHyphenator::PatternNode::initLevels(HyphenationLevels* levels) {
    levels_ = levels;
    ownLevels_ = true;
}

void WordHyphenator::PatternNode::linkLevels(HyphenationLevels* levels) {
    if (levels) {
        if (levels_) {
            HyphenationLevels::applyAtEnd(*levels_, *levels);
        } else {
            levels_ = levels;
        }
    }
}

const WordHyphenator::PatternNode* WordHyphenator::PatternNode::nextNode(UChar ch) const {
    for (const PatternNode* it = this; ; it = it->suffixLink_) {
        const PatternNode* child = it->childByChar(ch);
        if (child) {
            return child;
        } else if (it == it->suffixLink_) {
            return it;
        }
    }
    return 0;
}

void WordHyphenator::PatternNode::shrinkToFit() {
    children_.shrinkToFit();
}


/* ------------------------------------------ TypoHyphenator::Builder ------------------------------------------ */

WordHyphenator::Builder::Builder() :
        alphabet_(new Alphabet()),
        root_(new PatternNode(0)) {
}

WordHyphenator::Builder::~Builder() {
    if (alphabet_) {
        delete alphabet_;
    }
    if (root_) {
        delete root_;
    }
}

WordHyphenator* WordHyphenator::Builder::build() {
    if (root_) {
        prepareTree();
        WordHyphenator* typoHyphenator = new WordHyphenator(alphabet_, root_);
        alphabet_ = 0;
        root_ = 0;
        return typoHyphenator;
    } else {
        return 0;
    }
}

void WordHyphenator::Builder::addPattern(const WTF::String& pattern) {
    parsePattern(pattern, tempLevelValues_, tempLetters_);

    for (unsigned int i = 0; i < tempLetters_.size(); i++) {
        UChar ch = tempLetters_[i];
        if (ch != EDGE_OF_WORD) {
            alphabet_->addChar(ch);
        }
    }

    makeNode(tempLetters_)->initLevels(HyphenationLevels::create(tempLevelValues_));
}
void WordHyphenator::Builder::parsePattern(
        const WTF::String& pattern,
        /* out */ vector<u_int8_t>& levelValues, /* out */ vector<UChar>& letters) {
    levelValues.clear();
    levelValues.resize(pattern.length() + 1, (u_int8_t) 0);
    letters.resize(pattern.length());

    unsigned int letterCount = 0;
    for (unsigned int i = 0; i < pattern.length(); i++) {
        UChar ch = pattern[i];
        if (u'0' <= ch && ch <= u'9') {
            levelValues[letterCount] = ch - u'0';
        } else {
            letters[letterCount++] = ch;
        }
    }

    levelValues.resize(letterCount + 1);
    letters.resize(letterCount);
}

WordHyphenator::PatternNode* WordHyphenator::Builder::makeNode(const vector<UChar>& chars) {
    /* Т.к. в паттернах Tex буквы нижнего регистра содержат ровно один символ (1 char), а верхнего регистра могут содержать
     * 3 символа (3 char), то для облегчения работы, легче все преобразовать в верхний регистр (см. addPattern).
     * Т.к. при поиске переносов, все слово можно тоже просто перевести в верхний регистр */

    PatternNode* node = root_;
    for (UChar ch : chars) {
        const UChar* upperChars = alphabet_->upper(ch);
        if (upperChars) {
            u_int8_t upperCount = alphabet_->upperCount(ch);
            for (unsigned int j = 0; j < upperCount; j++) {
                node = makeChild(node, upperChars[j]);
            }
        } else {
            node = makeChild(node, ch);
        }
    }
    return node;
}

WordHyphenator::PatternNode* WordHyphenator::Builder::makeChild(PatternNode* node, UChar ch) {
    PatternNode* child = node->childByChar(ch);
    if (!child) {
        child = new PatternNode(ch);
        node->addChild(child);
    }
    return child;
}

void WordHyphenator::Builder::prepareTree() {
    root_->setSuffixLink(root_);
    queue<PatternNode*> queue;
    queue.push(root_);
    while (!queue.empty()) {
        PatternNode* node = queue.front();
        queue.pop();
        for (unsigned int i = 0; i < node->childCount(); i++) {
            PatternNode* child = node->childAt(i);
            prepareChild(node, child);
            queue.push(child);
        }
    }
}

void WordHyphenator::Builder::prepareChild(PatternNode* node, PatternNode* child) {
    auto suffixLink = findSuffixForChild(node, child->ch());
    child->setSuffixLink(suffixLink);
    child->linkLevels(suffixLink->levels());
    child->shrinkToFit();
}

WordHyphenator::PatternNode* WordHyphenator::Builder::findSuffixForChild(PatternNode* node, UChar childChar) {
    PatternNode* it = node;
    while (it != it->suffixLink()) {
        it = it->suffixLink();
        PatternNode* child = it->childByChar(childChar);
        if (child) {
            return child;
        }
    }
    return it;
}


}
