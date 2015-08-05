#pragma once

#include "public/platform/WebString.h"
#include <vector>

namespace typo {

class WordHyphenator {
    /**
     * Для нахождения переносов используется алгоритм Ляна-Кнута.
     * Для поиска соответствий паттернов к слову используется алгоритм Ахо-Корасик.
     */
private:
    class HyphenationLevels {
    public:
        HyphenationLevels();
        ~HyphenationLevels();
        void reserve(unsigned int capacity);
        inline void reset(unsigned int length);
        inline bool isOdd(unsigned int index) const;

        static void applyAtEndIndex(HyphenationLevels& destination, const HyphenationLevels& source, unsigned int endIndex);
        static void applyAtEnd(HyphenationLevels& destination, const HyphenationLevels& source);
        inline static unsigned int wordIndexToLevelIndex(unsigned int wordIndex);

        static HyphenationLevels* create(std::vector<u_int8_t>& values);

    private:
        HyphenationLevels(u_int8_t* values, unsigned int length, unsigned int capacity);

        u_int8_t* values_ = 0;
        unsigned int length_ = 0;
        unsigned int capacity_ = 0;
    };

    class Alphabet {
    public:
        Alphabet();
        ~Alphabet();
        void addChar(blink::WebUChar lowerChar);
        inline bool contains(blink::WebUChar ch) const;
        inline const blink::WebUChar* upper(blink::WebUChar ch) const;
        inline u_int8_t upperCount(blink::WebUChar ch) const;

    private:
        static const int MAX_CHAR = 65536;

        inline static void getUpperChars(blink::WebUChar ch, /* out */ blink::WebUChar*& upperChars, unsigned int& charCount);

        blink::WebUChar* lowerToUpper_[MAX_CHAR];
        u_int8_t lowerToUpperCount_[MAX_CHAR];
        bool chars_[MAX_CHAR];
    };

    class PatternNode;

    class PatternNodeCollection {
    public:
        PatternNodeCollection();
        ~PatternNodeCollection();
        inline PatternNode* getByChar(blink::WebUChar ch) const;
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

    class PatternNode {
    public:
        PatternNode(blink::WebUChar ch);
        ~PatternNode();

        inline blink::WebUChar ch() const { return char_; }

        inline void addChild(PatternNode* node);
        inline PatternNode* childByChar(blink::WebUChar ch) const;
        inline PatternNode* childAt(unsigned int index) const { return children_[index]; }
        inline unsigned int childCount() const { return children_.size(); }

        inline PatternNode* suffixLink() { return suffixLink_; }
        inline void setSuffixLink(PatternNode* suffixLink) { suffixLink_ = suffixLink; }

        inline HyphenationLevels* levels() const  { return levels_; }
        inline void initLevels(HyphenationLevels* levels);
        inline void linkLevels(HyphenationLevels* levels);

        inline void shrinkToFit();

        inline const PatternNode* nextNode(blink::WebUChar ch) const;

    private:
        const blink::WebUChar char_;
        PatternNodeCollection children_;
        PatternNode* suffixLink_ = 0;
        HyphenationLevels* levels_ = 0;
        bool ownLevels_ = false;
    };

public:
    class Builder {
    public:
        Builder();
        ~Builder();
        void addPattern(const blink::WebUChar* chars, unsigned int offset, unsigned int length);
        WordHyphenator* build();

    private:
        void parsePattern(
                const blink::WebUChar* chars, unsigned int offset, unsigned int length,
                /* out */ std::vector<u_int8_t>& levelValues, /* out */ std::vector<blink::WebUChar>& letters);
        PatternNode* makeNode(const std::vector<blink::WebUChar>& chars);
        PatternNode* makeChild(PatternNode* node, blink::WebUChar ch);
        void prepareTree();
        void prepareChild(PatternNode* node, PatternNode* child);
        PatternNode* findSuffixForChild(PatternNode* node, blink::WebUChar childChar);

        Alphabet* alphabet_;
        PatternNode* root_;

        std::vector<u_int8_t> tempLevelValues_;
        std::vector<blink::WebUChar> tempLetters_;
    };

    ~WordHyphenator();

    bool alphabetContains(blink::WebUChar ch);
    int hyphenateWord(const blink::WebString& word, unsigned int start, unsigned int end, unsigned int minBreakIndex, unsigned int maxBreakIndex);

private:
    WordHyphenator(Alphabet* alphabet, PatternNode* root);

    void toUpperCase(
            const blink::WebString& word, unsigned int start, unsigned int end,
            /* out */ std::vector<blink::WebUChar>& upperCaseChars,
            /* out */ std::vector<unsigned int>& originalIndices) const;
    void computeWordLevels(
            const std::vector<blink::WebUChar>& upperCaseChars,
            /* out */ HyphenationLevels& wordLevels) const;
    int indexOfBreak(
            const HyphenationLevels& wordLevels, std::vector<unsigned int>& originalIndices,
            unsigned int wordLength,
            unsigned int minBreakIndex, unsigned int maxBreakIndex) const;

    static const blink::WebUChar EDGE_OF_WORD = '.';  // символ, означающий начало или конец слова
    static const unsigned int MAX_WORD_LENGTH = 32;

    Alphabet* alphabet_;
    PatternNode* root_;

    // вспомогательные переменные
    std::vector<blink::WebUChar> upperCaseChars_;
    std::vector<unsigned int> originalIndices_;
    HyphenationLevels wordLevels_;
};

};
