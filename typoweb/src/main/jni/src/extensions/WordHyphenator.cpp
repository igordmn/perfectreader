#include "WordHyphenator.h"

#include "../util/Debug.h"
#include <algorithm>
#include <math.h>
#include <queue>
#include <unicode/ustring.h>

namespace typo {

using namespace std;
using namespace blink;

/* ------------------------------------------ TypoHyphenator ------------------------------------------ */

WordHyphenator::WordHyphenator(Alphabet* alphabet, PatternNode* root) :
        alphabet_(alphabet), root_(root) {
    upperCaseChars_.reserve(MAX_WORD_LENGTH);
    originalIndices_.reserve(MAX_WORD_LENGTH);
    wordLevels_.reserve(HyphenationLevels::wordIndexToLevelIndex(MAX_WORD_LENGTH));
}

WordHyphenator::~WordHyphenator() {
    delete alphabet_;
    delete root_;
}

bool WordHyphenator::alphabetContains(blink::WebUChar ch) {
    return alphabet_->contains(ch);
}

int WordHyphenator::hyphenateWord(
        const WebString& text, unsigned int start, unsigned int end, unsigned int minBreakIndex, unsigned int maxBreakIndex) {
    unsigned int length = end >= start ? end - start : 0;

    if (length > MAX_WORD_LENGTH) {
        length = MAX_WORD_LENGTH;
        end = start + length;
    }

    upperCaseChars_.clear();
    originalIndices_.clear();
    wordLevels_.reset(HyphenationLevels::wordIndexToLevelIndex(length));

    toUpperCase(text, start, end, upperCaseChars_, originalIndices_);
    computeWordLevels(upperCaseChars_, wordLevels_);
    return indexOfBreak(wordLevels_, originalIndices_, length, minBreakIndex, maxBreakIndex);
}

void WordHyphenator::toUpperCase(
        const WebString& word, unsigned int start, unsigned int end,
        /* out */ vector<WebUChar>& upperCaseChars,
        /* out */ vector<unsigned int>& originalIndices) const {
    for (unsigned int i = start; i < end; i++) {
        WebUChar ch = word.at(i);
        const WebUChar* upperChars = alphabet_->upper(ch);
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
        const vector<WebUChar>& upperCaseChars,
        /* out */ HyphenationLevels& wordLevels) const {
    const PatternNode* node = root_;
    int size = (int) upperCaseChars.size();
    for (int i = -1; i < size + 1; i++) {
        bool isEdge = i == -1 || i == size;
        node = node->nextNode(isEdge ? EDGE_OF_WORD : upperCaseChars[i]);
        const HyphenationLevels* levels = node->levels();
        if (levels) {
            HyphenationLevels::applyAtEndIndex(wordLevels, *levels, HyphenationLevels::wordIndexToLevelIndex(i + 1));
        }
    }
}

int WordHyphenator::indexOfBreak(
        const HyphenationLevels& wordLevels, vector<unsigned int>& originalIndices,
        unsigned int wordLength,
        unsigned int minBreakIndex, unsigned int maxBreakIndex) const {
    // одну букву нельзя переносить или оставлять
    if (wordLength >= 3) {
        for (unsigned int i = wordLength - 2; i >= 2; i--) {
            if (wordLevels.isOdd(i)) {
                unsigned int originalIndex = originalIndices[i];
                if (minBreakIndex <= originalIndex && originalIndex <= maxBreakIndex) {
                    return originalIndex;
                }
            }
        }
    }

    return -1;
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

void WordHyphenator::HyphenationLevels::reset(unsigned int length) {
    reserve(length);
    length_ = length;
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

void WordHyphenator::HyphenationLevels::applyAtEndIndex(HyphenationLevels& destination, const HyphenationLevels& source, unsigned int endIndex) {
    int beginIndex = endIndex - source.length_;
    unsigned int destBegin = max(0, beginIndex);
    unsigned int destEnd = min(destination.length_, endIndex);
    unsigned int sourceBegin = destBegin - beginIndex;
    for (unsigned int i = destBegin, k = sourceBegin; i < destEnd; ++i, ++k) {
        destination.values_[i] = max(destination.values_[i], source.values_[k]);
    }
}

void WordHyphenator::HyphenationLevels::applyAtEnd(HyphenationLevels& destination, const HyphenationLevels& source) {
    applyAtEndIndex(destination, source, destination.length_);
}

WordHyphenator::HyphenationLevels* WordHyphenator::HyphenationLevels::create(vector<u_int8_t>& values) {
    u_int8_t* levelValues = new u_int8_t[values.size()];
    copy(values.begin(), values.end(), levelValues);
    return new HyphenationLevels(levelValues, values.size(), values.size());
}

bool WordHyphenator::HyphenationLevels::isOdd(unsigned int index) const {
    u_int8_t level = index >= 0 && index < length_ ? values_[index] : 0;
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
        WebUChar* upperChars = lowerToUpper_[i];
        if (upperChars) {
            delete[] upperChars;
        }
    }
}

void WordHyphenator::Alphabet::addChar(WebUChar lowerChar) {
    if (!contains(lowerChar)) {
        WebUChar* upperChars = 0;
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

void WordHyphenator::Alphabet::getUpperChars(WebUChar lowerChar, /* out */ WebUChar*& upperChars, /* out */ unsigned int& upperCharCount) {
    UErrorCode status = U_ZERO_ERROR;
    upperCharCount = u_strToUpper(0, 0,  &lowerChar, 1, "", &status);
    if (upperCharCount > 0) {
        upperChars = new WebUChar[upperCharCount];
        status = U_ZERO_ERROR;
        u_strToUpper(upperChars, upperCharCount,  &lowerChar, 1, "", &status);
        if (U_FAILURE(status)) {
            delete upperChars;
            upperChars = 0;
            upperCharCount = 0;
        }
    }
}

bool WordHyphenator::Alphabet::contains(WebUChar ch) const {
    return chars_[ch];
}

const WebUChar* WordHyphenator::Alphabet::upper(WebUChar ch) const {
    return lowerToUpper_[ch];
}

u_int8_t WordHyphenator::Alphabet::upperCount(WebUChar ch) const {
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

WordHyphenator::PatternNode* WordHyphenator::PatternNodeCollection::getByChar(WebUChar ch) const {
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

WordHyphenator::PatternNode::PatternNode(WebUChar ch) :
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

WordHyphenator::PatternNode* WordHyphenator::PatternNode::childByChar(WebUChar ch) const  {
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

const WordHyphenator::PatternNode* WordHyphenator::PatternNode::nextNode(WebUChar ch) const {
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

void WordHyphenator::Builder::addPattern(const blink::WebUChar* chars, unsigned int offset, unsigned int length) {
    parsePattern(chars, offset, length, tempLevelValues_, tempLetters_);

    for (unsigned int i = 0; i < tempLetters_.size(); i++) {
        WebUChar ch = tempLetters_[i];
        if (ch != EDGE_OF_WORD) {
            alphabet_->addChar(ch);
        }
    }

    makeNode(tempLetters_)->initLevels(HyphenationLevels::create(tempLevelValues_));
}
void WordHyphenator::Builder::parsePattern(
        const blink::WebUChar* chars, unsigned int offset, unsigned int length,
        /* out */ vector<u_int8_t>& levelValues, /* out */ vector<WebUChar>& letters) {
    levelValues.clear();
    levelValues.resize(length + 1, (u_int8_t) 0);
    letters.resize(length);

    unsigned int letterCount = 0;
    for (unsigned int i = offset; i < length; i++) {
        WebUChar ch = chars[i];
        if (u'0' <= ch && ch <= u'9') {
            levelValues[letterCount] = ch - u'0';
        } else {
            letters[letterCount++] = ch;
        }
    }

    levelValues.resize(letterCount + 1);
    letters.resize(letterCount);
}

WordHyphenator::PatternNode* WordHyphenator::Builder::makeNode(const vector<WebUChar>& chars) {
    /* Т.к. в паттернах Tex буквы нижнего регистра содержат ровно один символ (1 char), а верхнего регистра могут содержать
     * 3 символа (3 char), то для облегчения работы, легче все преобразовать в верхний регистр (см. addPattern).
     * Т.к. при поиске переносов, все слово можно тоже просто перевести в верхний регистр */

    PatternNode* node = root_;
    for (WebUChar ch : chars) {
        const WebUChar* upperChars = alphabet_->upper(ch);
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

WordHyphenator::PatternNode* WordHyphenator::Builder::makeChild(PatternNode* node, WebUChar ch) {
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

WordHyphenator::PatternNode* WordHyphenator::Builder::findSuffixForChild(PatternNode* node, WebUChar childChar) {
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
