# PreVisit Call‑E Coordinator — Project Purpose

## Problem

Clinic staff lose time collecting basic pre-visit information and coordinating appointments. Patients may arrive with incomplete information, while staff lack a clear summary before the visit.

## Our Solution

PreVisit Call‑E Coordinator is a CALL‑E-powered workflow for primary-care appointment coordination.

It calls a consenting patient, collects structured pre-visit information, checks appointment availability, receives explicit verbal confirmation of the exact appointment time, and produces a staff-ready summary.

## What the System Does

1. A staff member starts an intake case for an existing patient.
2. CALL‑E calls the patient.
3. The patient provides:
   - reason for visit
   - symptoms and duration
   - current medications
   - relevant previous conditions
   - preferred appointment times
   - language or accessibility needs
4. The system identifies possible emergency red flags.
5. If an emergency red flag appears, the system stops intake and scheduling, gives immediate emergency-care guidance, and flags the case for staff follow-up.
6. For non-emergency cases, the system proposes an available appointment slot.
7. The appointment is created only after the patient verbally confirms the exact date and time.
8. Clinic staff receive a structured summary before the patient arrives.

## Staff View

The dashboard shows:

- patient intake summary
- urgency status
- appointment date, time, provider, and location
- patient confirmation status
- missing or unanswered information
- review status for clinic staff
- CALL‑E result evidence

## Safety Boundaries

- The system does not diagnose medical conditions.
- The system does not replace emergency services or clinical judgment.
- It does not schedule a possible emergency case.
- The demo uses fake patients or consenting teammates only.
- Staff review the information before clinical action.

## Why This Is Different

This is not a generic AI scheduler. It connects a real phone conversation with structured pre-visit intake, safe scheduling confirmation, and a clinic-ready handoff workflow.

## Demo Goal

Show one patient journey from staff-started CALL‑E intake call to a verbally confirmed appointment and a staff-ready summary.
