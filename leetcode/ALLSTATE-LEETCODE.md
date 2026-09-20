# Allstate — the complete tagged LeetCode list

**All 31 problems currently tagged to Allstate**, pulled directly from the company tag
dataset: **8 Easy · 16 Medium · 7 Hard**.

*Frequency* is how often the problem shows up in Allstate-tagged reports relative to the
others — it's a ranking signal, not a probability. Work top-down.

**Top topics at Allstate:** Array · String · Hash Table · Two Pointers · Sorting

> ⚠️ **Two separate sources, and you need both.** This table is the *tag dataset*.
> Separately, candidates reported problems in the live technical screen that are **not**
> in this table — those are in [§ Reported in interviews](#also-reported-in-actual-interviews-not-in-the-tag-list)
> below, and they're arguably higher value because a human actually asked them.

---

## Tier 1 — do these first (frequency ≥ 78%)

The top 3 are named explicitly in the tag summary as Allstate's most common.

| # | Problem | Diff | Freq | Topics |
|---|---------|------|------|--------|
| 1 | [String Compression](https://leetcode.com/problems/string-compression) | Medium | **100%** | Two Pointers, String |
| 2 | [Maximum Size Subarray Sum Equals k](https://leetcode.com/problems/maximum-size-subarray-sum-equals-k) | Medium | **84%** | Array, Hash Table, Prefix Sum |
| 3 | [Merge Intervals](https://leetcode.com/problems/merge-intervals) | Medium | **83%** | Array, Sorting |
| 4 | [Closest Binary Search Tree Value](https://leetcode.com/problems/closest-binary-search-tree-value) | Easy | 81% | Binary Search, Tree, DFS |
| 5 | [Valid Palindrome](https://leetcode.com/problems/valid-palindrome) | Easy | 79% | Two Pointers, String |
| 6 | [Custom Sort String](https://leetcode.com/problems/custom-sort-string) | Medium | 78% | Hash Table, String, Sorting |

✅ **#1, #2, #3 are already in your dojo** with full test suites — `StringProblemsTest` and `ArrayProblemsTest`.

---

## Tier 2 — high value (frequency 54–68%)

| # | Problem | Diff | Freq | Topics |
|---|---------|------|------|--------|
| 7 | [Find Leaves of Binary Tree](https://leetcode.com/problems/find-leaves-of-binary-tree) | Medium | 68% | Tree, DFS |
| 8 | [Minimum Time to Visit a Cell In a Grid](https://leetcode.com/problems/minimum-time-to-visit-a-cell-in-a-grid) | Hard | 63% | Array, BFS, Heap, Matrix |
| 9 | [K-diff Pairs in an Array](https://leetcode.com/problems/k-diff-pairs-in-an-array) | Medium | 59% | Array, Hash Table, Two Pointers |
| 10 | [Find Median from Data Stream](https://leetcode.com/problems/find-median-from-data-stream) | Hard | 59% | Design, Two Heaps |
| 11 | [Merge Two Sorted Lists](https://leetcode.com/problems/merge-two-sorted-lists) | Easy | 57% | Linked List, Recursion |
| 12 | [Reverse Nodes in k-Group](https://leetcode.com/problems/reverse-nodes-in-k-group) | Hard | 56% | Linked List, Recursion |
| 13 | [Jump Game](https://leetcode.com/problems/jump-game) | Medium | 55% | Array, DP, Greedy |
| 14 | [Copy List with Random Pointer](https://leetcode.com/problems/copy-list-with-random-pointer) | Medium | 55% | Hash Table, Linked List |
| 15 | [Check if One String Swap Can Make Strings Equal](https://leetcode.com/problems/check-if-one-string-swap-can-make-strings-equal) | Easy | 54% | Hash Table, String, Counting |
| 16 | [Final Array State After K Multiplication Operations I](https://leetcode.com/problems/final-array-state-after-k-multiplication-operations-i) | Easy | 54% | Array, Math, Heap |
| 17 | [Decode String](https://leetcode.com/problems/decode-string) | Medium | 54% | String, Stack, Recursion |

**Note #10 and #14** — `Find Median from Data Stream` is a *design* problem, the same
category as LRU Cache, which candidates report being asked live. `Copy List with Random
Pointer` is the deep-copy question in code form, which pairs with the shallow-vs-deep-copy
theory question Allstate asks. Both punch above their frequency.

---

## Tier 3 — cover if you have time (frequency 24–49%)

| # | Problem | Diff | Freq | Topics |
|---|---------|------|------|--------|
| 18 | [Identify the Largest Outlier in an Array](https://leetcode.com/problems/identify-the-largest-outlier-in-an-array) | Medium | 49% | Array, Hash Table, Counting |
| 19 | [Zigzag Conversion](https://leetcode.com/problems/zigzag-conversion) | Medium | 48% | String |
| 20 | [K Closest Points to Origin](https://leetcode.com/problems/k-closest-points-to-origin) | Medium | 45% | Heap, Sorting, QuickSelect |
| 21 | [Sliding Window Maximum](https://leetcode.com/problems/sliding-window-maximum) | Hard | 44% | Array, Deque, Sliding Window |
| 22 | [Shortest Word Distance II](https://leetcode.com/problems/shortest-word-distance-ii) | Medium | 43% | Design, Hash Table, Two Pointers |
| 23 | [Basic Calculator](https://leetcode.com/problems/basic-calculator) | Hard | 42% | Math, String, Stack, Recursion |
| 24 | [Kth Smallest Element in a BST](https://leetcode.com/problems/kth-smallest-element-in-a-bst) | Medium | 40% | Tree, DFS, Inorder |
| 25 | [Remove Element](https://leetcode.com/problems/remove-element) | Easy | 36% | Array, Two Pointers |
| 26 | [Majority Element](https://leetcode.com/problems/majority-element) | Easy | 32% | Array, Hash Table, Boyer-Moore |
| 27 | [Rank Transform of a Matrix](https://leetcode.com/problems/rank-transform-of-a-matrix) | Hard | 31% | Union Find, Matrix, Sorting |
| 28 | [Missing Ranges](https://leetcode.com/problems/missing-ranges) | Easy | 28% | Array |
| 29 | [Permutation in String](https://leetcode.com/problems/permutation-in-string) | Medium | 27% | Hash Table, Sliding Window |
| 30 | [Word Break II](https://leetcode.com/problems/word-break-ii) | Hard | 24% | DP, Backtracking, Memoization |
| 31 | [Construct Quad Tree](https://leetcode.com/problems/construct-quad-tree) | Medium | 24% | Divide and Conquer, Tree |

---

## Also reported in actual interviews (not in the tag list)

Candidates described being asked these **live, by a human**. Treat them as Tier 1.

| Problem | Where it was reported | In your dojo? |
|---|---|---|
| [Longest Substring Without Repeating Characters](https://leetcode.com/problems/longest-substring-without-repeating-characters) | Named in Allstate's technical screen | ✅ `StringProblemsTest` |
| [LRU Cache](https://leetcode.com/problems/lru-cache) | Named: *"LRU Cache design/code problems"* | ✅ `LRUCacheTest` |
| [Search in Rotated Sorted Array](https://leetcode.com/problems/search-in-rotated-sorted-array) | Asked verbatim: *"find the index of 3 in a rotated array with the **best algorithm**"* | ✅ `ArrayProblemsTest` |
| [Find Minimum in Rotated Sorted Array](https://leetcode.com/problems/find-minimum-in-rotated-sorted-array) | Asked verbatim: *"what is a pivot in a rotated array and how to find it?"* | ✅ `ArrayProblemsTest` |
| [Two Sum](https://leetcode.com/problems/two-sum) | Standard warm-up | ✅ `ArrayProblemsTest` |
| **Implement a Set with no collections** | The TDD pair-programming round | ✅ `MySetSpecTest` |

---

## What the list actually tells you

**Correcting something I told you earlier:** I said graphs and DP weren't worth your time.
The tag list proves that too strong. Arrays/strings/hash tables *do* dominate the top, but
the tail is broader than I implied:

- **Trees: 4 problems** (#4, #7, #24, #31) — know BST properties and inorder traversal
- **Linked lists: 3** (#11, #12, #14) — know the dummy-head and two-pointer patterns
- **Stack: 2 Hards** (#17, #23) — both parsing problems; learn the one recursive pattern and both fall
- **Heap: 4** (#8, #10, #16, #20) — `PriorityQueue` in Java, and the two-heap median trick
- **DP: 2** (#13, #30) — Jump Game is really greedy; only Word Break II is true DP
- **Union Find: 1** (#27) — safe to skip at your level

### Suggested split for Day 5 (your DSA simulation day)

1. **Re-solve Tier 1 from scratch, timed, out loud** — you've built them once, so this is recall, not learning. 60 min.
2. **Two-pointer + sliding window set**: #5, #9, #29, #21. These four share one pattern. 45 min.
3. **Tree set**: #4, #24 (both are BST + inorder). 30 min.
4. **Linked list set**: #11, #14. 30 min.
5. **Stack set**: #17 (do `Basic Calculator` only if #17 felt easy). 30 min.

Skip #27 and #31 unless you're already comfortable. The rounds are rated **2.6/5**
difficulty — nobody is failing this loop on Union Find.

### How to practise, given their culture

Allstate is an XP/TDD shop, so for every problem:
1. **State the complexity before you write code.** They probe this explicitly.
2. **List the edge cases out loud first** — empty, single element, all-duplicates, null.
3. **Write one test case by hand** before the solution, even on LeetCode. It's the habit they're screening for.

Source: [Allstate LeetCode tag dataset, Interview Solver](https://interviewsolver.com/interview-questions/allstate)
