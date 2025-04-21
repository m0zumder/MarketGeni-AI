# API Endpoints Reference

## Introduction
This document provides detailed information about all available endpoints in the AI Marketing Platform API. Each endpoint includes request/response formats, parameters, and example usage.

## Campaign Endpoints

### Create Campaign
```http
POST /campaigns
```

#### Request Body
```json
{
  "name": "string",
  "objective": "string",
  "budget": "number",
  "startDate": "string (ISO 8601)",
  "endDate": "string (ISO 8601)",
  "targetAudience": {
    "demographics": "object",
    "interests": "array"
  }
}
```

#### Response
```json
{
  "status": "success",
  "data": {
    "campaignId": "string",
    "name": "string",
    "status": "string"
  }
}
```

### Get Campaign Details
```http
GET /campaigns/{id}
```

#### Response
```json
{
  "status": "success",
  "data": {
    "campaignId": "string",
    "name": "string",
    "objective": "string",
    "budget": "number",
    "metrics": {
      "impressions": "number",
      "clicks": "number",
      "conversions": "number"
    }
  }
}
```

## Video Processing Endpoints

### Submit Video Edit Job
```http
POST /video/edit
```

#### Request Body
```json
{
  "videoUrl": "string",
  "params": {
    "trim": {
      "start": "number",
      "end": "number"
    },
    "filters": ["string"],
    "effects": ["string"]
  }
}
```

#### Response
```json
{
  "status": "success",
  "data": {
    "jobId": "string",
    "status": "string"
  }
}
```

### Get Video Job Status
```http
GET /video/job/{jobId}
```

#### Response
```json
{
  "status": "success",
  "data": {
    "jobId": "string",
    "status": "string",
    "progress": "number",
    "result": {
      "url": "string"
    }
  }
}
```

## Content Generation Endpoints

### Generate Content
```http
POST /content/generate
```

#### Request Body
```json
{
  "type": "string",
  "topic": "string",
  "parameters": {
    "wordCount": "number",
    "tone": "string",
    "keywords": ["string"]
  }
}
```

#### Response
```json
{
  "status": "success",
  "data": {
    "content": "string",
    "metadata": {
      "wordCount": "number",
      "readingTime": "string"
    }
  }
}
```

## Analytics Endpoints

### Get Campaign Analytics
```http
GET /analytics/campaign/{id}
```

#### Query Parameters
- `startDate`: string (ISO 8601)
- `endDate`: string (ISO 8601)
- `metrics`: array of strings

#### Response
```json
{
  "status": "success",
  "data": {
    "campaignId": "string",
    "timeframe": {
      "start": "string",
      "end": "string"
    },
    "metrics": {
      "impressions": "number",
      "clicks": "number",
      "conversions": "number",
      "ctr": "number",
      "cpa": "number"
    }
  }
}
```

### Get Social Media Metrics
```http
GET /analytics/social
```

#### Query Parameters
- `platforms`: array of strings
- `startDate`: string (ISO 8601)
- `endDate`: string (ISO 8601)

#### Response
```json
{
  "status": "success",
  "data": {
    "platforms": {
      "facebook": {
        "engagement": "number",
        "reach": "number",
        "shares": "number"
      },
      "twitter": {
        "engagement": "number",
        "impressions": "number",
        "retweets": "number"
      }
    }
  }
}
```

## Error Responses
All endpoints follow the same error response format:

```json
{
  "status": "error",
  "error": {
    "code": "string",
    "message": "string"
  }
}
```

### Common Error Codes
- `invalid_request`: Request parameters are invalid
- `not_found`: Requested resource not found
- `unauthorized`: Authentication failed
- `forbidden`: Insufficient permissions
- `rate_limit_exceeded`: API rate limit exceeded