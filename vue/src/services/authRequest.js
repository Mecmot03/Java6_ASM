const toBearerToken = (token) => token.startsWith('Bearer ') ? token : `Bearer ${token}`

export const getAuthConfig = () => {
  const token = localStorage.getItem('token')
  if (!token) {
    return {}
  }

  return {
    headers: {
      Authorization: toBearerToken(token)
    }
  }
}

export const isAuthFailure = (error) => {
  const status = error?.response?.status
  return status === 401 || status === 403
}

export const requestWithOptionalAuth = async (requestWithoutAuth, requestWithAuth) => {
  try {
    return await requestWithoutAuth()
  } catch (error) {
    if (!isAuthFailure(error) || !localStorage.getItem('token')) {
      throw error
    }

    return requestWithAuth()
  }
}