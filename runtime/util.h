
#ifndef SRIC_UTIL_H_
#define SRIC_UTIL_H_

#include "Ptr.h"
#include "Refable.h"

namespace sric {


	template<typename T>
	T* unsafeAlloc() {
		return new T();
	}

	template<typename T>
	void unsafeFree(T* p) {
		delete p;
	}

	template<typename T>
	bool ptrIs(void* p) {
		return dynamic_cast<T*>(p) != nullptr;
	}

	template<typename T, typename F>
	bool ptrIs(F p) {
		return dynamic_cast<T*>(p.get()) != nullptr;
	}

	template<typename T>
	T* nonNullable(T* p) {
		sc_assert(p != nullptr, "Non-Nullable");
		return p;
	}

	template<typename T>
	T& nonNullable(T& p) {
		sc_assert(!p.isNull(), "Non-Nullable");
		return p;
	}

	inline bool isNull(void* p) {
		return p == nullptr;
	}
}

#endif