#pragma once

#include "Debug.h"
#include <list>
#include <map>
#include <unordered_map>
#include <functional>
#include <memory>

#define HASH_FUNCTION HASH_SFH

#include "../thirdParty/uthash/uthash.h"

using namespace std;

namespace dmi {
    template<typename Key, typename Value>
    class LRUCache {
    private:
        struct Entry {
            Key key;
            Value *value;
            UT_hash_handle hh;

            Entry *lessUsedEntry;
            Entry *moreUsedEntry;
        };

        uint32_t maxWeight;
        function<uint32_t(const Key &, Value *)> weighEntry;
        function<void(const Key &, Value *)> destroyEntry;

        uint32_t weight = 0;
        Entry *cache = 0;
        Entry *leastUsedEntry = 0;
        Entry *mostUsedEntry = 0;

    public:
        LRUCache(
                uint32_t maxWeight,
                function<uint32_t(const Key &, Value *)> weighEntry,
                function<void(const Key &, Value *)> destroyEntry
        ) :
                maxWeight(maxWeight),
                weighEntry(weighEntry),
                destroyEntry(destroyEntry) {
        }

        ~LRUCache() {
            Entry *entry, *tmpEntry;
            HASH_ITER(hh, cache, entry, tmpEntry) {
                HASH_DEL(cache, entry);
                destroyEntry(entry->key, entry->value);
                free(entry);
            }
        }

        void put(const Key &key, Value *value) {
            uint32_t entryWeight = weighEntry(key, value);
            evictFor(entryWeight);
            weight += entryWeight;

            Entry *entry = (Entry *) malloc(sizeof(Entry));
            memset(entry, 0, sizeof(Entry));
            entry->key = key;
            entry->value = value;

            addMostUsed(entry);
            HASH_ADD(hh, cache, key, sizeof(Key), entry);
        }

        inline Value *get(const Key &key) {
            struct Entry *entry = 0;
            HASH_FIND(hh, cache, &key, sizeof(Key), entry);
            if (entry) {
                removeUsed(entry);
                addMostUsed(entry);
                return entry->value;
            } else {
                return 0;
            }
        }

    private:
        LRUCache(const LRUCache &cache) = delete;

        inline void evictFor(uint32_t entryWeight) {
            Entry *entry = leastUsedEntry;

            while (weight + entryWeight > maxWeight && entry != 0) {
                Entry *moreUsed = entry->moreUsedEntry;

                weight -= weighEntry(entry->key, entry->value);

                removeUsed(entry);
                HASH_DEL(cache, entry);
                destroyEntry(entry->key, entry->value);
                free(entry);

                entry = moreUsed;
            }
        }

        inline void addMostUsed(Entry *entry) {
            if (leastUsedEntry == 0) {
                leastUsedEntry = entry;
                mostUsedEntry = entry;
            } else {
                entry->lessUsedEntry = mostUsedEntry;
                mostUsedEntry->moreUsedEntry = entry;
                mostUsedEntry = entry;
            }
        }

        inline void removeUsed(Entry *entry) {
            if (entry->lessUsedEntry != 0)
                entry->lessUsedEntry->moreUsedEntry = entry->moreUsedEntry;
            if (entry->moreUsedEntry != 0)
                entry->moreUsedEntry->lessUsedEntry = entry->lessUsedEntry;
            if (entry == leastUsedEntry)
                leastUsedEntry = entry->moreUsedEntry;
            if (entry == mostUsedEntry)
                mostUsedEntry = entry->lessUsedEntry;
            entry->lessUsedEntry = 0;
            entry->moreUsedEntry = 0;
        }
    };
}