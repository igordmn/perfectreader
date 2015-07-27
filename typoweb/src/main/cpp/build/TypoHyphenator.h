#ifndef TypoHyphenator_h
#define TypoHyphenator_h

#include "wtf/HashMap.h"
#include "wtf/RefPtr.h"
#include "wtf/text/WTFString.h"
#include "wtf/unicode/Unicode.h"
#include <vector>

namespace blink {

void TEST_TEST_TEST_HYPHENATE();

class WordHyphenator {
    /* Используется алгоритм Ахо-Корасик */

public:
    class Builder;
    class Context;
    class Hyphens;

    ~WordHyphenator();

    bool alphabetContains(UChar ch) const;
    const Hyphens& hyphenateWord(Context& context, const UChar* word, unsigned int start, unsigned int end) const ;

private:
    class HyphenationLevels;
    class Alphabet;
    class PatternNodeCollection;
    class PatternNode;

    WordHyphenator(Alphabet* alphabet, PatternNode* root);

    void toUpperCase(
            const UChar* word, unsigned int start, unsigned int end,
            /* out */ std::vector<UChar>& upperCaseChars,
            /* out */ std::vector<unsigned int>& originalIndices) const;
    void computeWordLevels(
            const std::vector<UChar>& upperCaseChars,
            /* out */ HyphenationLevels& wordLevels) const;
    void createHyphens(
            const HyphenationLevels& wordLevels, std::vector<unsigned int>& originalIndices,
            /* out */ Hyphens& hyphens) const;

    static const UChar EDGE_OF_WORD = '.';  // символ, означающий начало или конец слова

    Alphabet* alphabet_;
    PatternNode* root_;
};

class WordHyphenator::HyphenationLevels {
public:
    HyphenationLevels();
    ~HyphenationLevels();
    void reserve(unsigned int capacity);
    inline void reset(unsigned int wordLength);
    inline bool isOdd(unsigned int wordIndex) const;

    static void applyAtEndWordIndex(HyphenationLevels& destination, const HyphenationLevels& source, unsigned int endWordIndex);
    static void applyAtEnd(HyphenationLevels& destination, const HyphenationLevels& source);

    static HyphenationLevels* create(std::vector<u_int8_t>& values);

private:
    HyphenationLevels(u_int8_t* values, unsigned int length, unsigned int capacity);

    static void applyAtEndIndex(HyphenationLevels& destination, const HyphenationLevels& source, unsigned int endIndex);
    inline static unsigned int wordIndexToLevelIndex(unsigned int wordIndex);

    u_int8_t* values_ = 0;
    unsigned int length_ = 0;
    unsigned int capacity_ = 0;
};

class WordHyphenator::Alphabet {
public:
    Alphabet();
    ~Alphabet();
    void addChar(UChar lowerChar);
    inline bool contains(UChar ch) const;
    inline const UChar* upper(UChar ch) const;
    inline u_int8_t upperCount(UChar ch) const;

private:
    static const int MAX_CHAR = 65536;

    inline static void getUpperChars(UChar ch, /* out */ UChar*& upperChars, unsigned int& charCount);

    UChar* lowerToUpper_[MAX_CHAR];
    u_int8_t lowerToUpperCount_[MAX_CHAR];
    bool chars_[MAX_CHAR];
};

class WordHyphenator::PatternNodeCollection {
public:
    PatternNodeCollection();
    ~PatternNodeCollection();
    inline PatternNode* getByChar(UChar ch) const;
    inline void add(PatternNode* value);
    inline unsigned int size() const { return size_; }
    inline PatternNode* operator[](unsigned int index) const { return items_[index]; }
    inline void shrinkToFit();

private:
    static const unsigned int BUCKET_COUNT = 32;
    static const unsigned int INITIAL_CAPACITY = 8;

    inline void setCapacity(unsigned int capacity);

    PatternNode** items_ = new PatternNode*[INITIAL_CAPACITY];
    u_int16_t capacity_ = INITIAL_CAPACITY;
    u_int16_t size_ = 0;
    u_int16_t bucketEnds_[BUCKET_COUNT];
};

class WordHyphenator::PatternNode {
public:
    PatternNode(UChar ch);
    ~PatternNode();

    inline UChar ch() const { return char_; }

    inline void addChild(PatternNode* node);
    inline PatternNode* childByChar(UChar ch) const;
    inline PatternNode* childAt(unsigned int index) const { return children_[index]; }
    inline unsigned int childCount() const { return children_.size(); }

    inline PatternNode* suffixLink() { return suffixLink_; }
    inline void setSuffixLink(PatternNode* suffixLink) { suffixLink_ = suffixLink; }

    inline HyphenationLevels* levels() const  { return levels_; }
    inline void initLevels(HyphenationLevels* levels);
    inline void linkLevels(HyphenationLevels* levels);

    inline void shrinkToFit();

    inline const PatternNode* nextNode(UChar ch) const;

private:
    const UChar char_;
    PatternNodeCollection children_;
    PatternNode* suffixLink_ = 0;
    HyphenationLevels* levels_ = 0;
    bool ownLevels_ = false;
};

class WordHyphenator::Hyphens {
public:
    Hyphens() {}

    inline void reserve(unsigned int capacity) { canHyphenate_.reserve(capacity); }
    inline void resize(unsigned int length) { canHyphenate_.resize(length); }

    inline bool canHyphenate(unsigned int index) const {
        return index >= 0 && index < canHyphenate_.size() && canHyphenate_[index];
    }

private:
    std::vector<bool> canHyphenate_;

    friend WordHyphenator;
};

class WordHyphenator::Context {
public:
    Context() {
        upperCaseChars_.reserve(INITIAL_WORD_LENGTH);
        originalIndices_.reserve(INITIAL_WORD_LENGTH);
        wordLevels_.reserve(INITIAL_WORD_LENGTH);
        hyphens_.reserve(INITIAL_WORD_LENGTH);
    }
    
    inline const Hyphens& hyphens() const { return hyphens_; }

private:
    static const unsigned int INITIAL_WORD_LENGTH = 16;

    std::vector<UChar> upperCaseChars_;
    std::vector<unsigned int> originalIndices_;
    HyphenationLevels wordLevels_;
    Hyphens hyphens_;

    friend WordHyphenator;
};

class WordHyphenator::Builder {
public:
    Builder();
    ~Builder();
    void addPattern(const WTF::String& pattern);
    WordHyphenator* build();

private:
    void parsePattern(
            const WTF::String& pattern,
            /* out */ std::vector<u_int8_t>& levelValues, /* out */ std::vector<UChar>& letters);
    PatternNode* makeNode(const std::vector<UChar>& chars);
    PatternNode* makeChild(PatternNode* node, UChar ch);
    void prepareTree();
    void prepareChild(PatternNode* node, PatternNode* child);
    PatternNode* findSuffixForChild(PatternNode* node, UChar childChar);

    Alphabet* alphabet_;
    PatternNode* root_;

    std::vector<u_int8_t> tempLevelValues_;
    std::vector<UChar> tempLetters_;
};


};

#endif // TypoHyphenator_h
