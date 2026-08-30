# PreVisit Call‑E Coordinator — Build Brief

## 1. Product Goal

Build a modern healthcare pre-visit coordination demo for the CALL‑E hackathon.

The system helps a primary-care clinic collect patient intake information by phone, safely schedule an appointment only after explicit verbal confirmation, and prepare a structured summary for clinic staff.

This is not a medical diagnosis system. It is a scheduling and pre-visit coordination workflow.

## 2. Current Repository Map

| Folder                                          | Role in this project                          | Decision                                                               |
| ----------------------------------------------- | --------------------------------------------- | ---------------------------------------------------------------------- |
| `previsit-coordinator/`                         | Our new project workspace                     | Build the new application here                                         |
| `Sources/call-e-integrations/`                  | CALL‑E integration documentation and examples | Reference for real CALL‑E integration                                  |
| `Sources/awesome-phone-call-agents/`            | Required public submission repository         | Prepare a reusable project contribution and submit a pull request here |
| `Sources/openmrs-module-appointmentscheduling/` | Legacy OpenMRS appointment module             | Reference only; do not run, modify, or depend on it                    |
| `Sources/openmrs-esm-patient-management/`       | OpenMRS patient-management frontend           | Reference for clinic workflow and UI ideas only                        |

## 3. Technology Direction

```text
Backend: Java 25 + latest stable Spring Boot 4.x
Build tool: Maven
Frontend: Separate clinic dashboard built by the frontend teammate
Phone workflow: Real CALL‑E integration
Scheduling: Mock appointment gateway first
OpenMRS: Reference concepts now; optional REST adapter later
```

The project must not depend on the legacy OpenMRS runtime. Its Spring 3 XML configuration and old platform version are not part of the new application.

## 4. What We Reuse from OpenMRS

We reuse concepts, not old implementation code:

- patient identity reference
- service type
- provider
- clinic location
- appointment date and time
- appointment status
- appointment priority
- schedule conflict checking
- staff review workflow

We do not reuse:

- legacy Spring XML
- old Maven configuration
- legacy OpenMRS server dependencies
- legacy database assumptions
- old UI code as a runtime dependency

## 5. Core User Journey

1. Clinic staff selects an existing demo patient and starts an intake case.
2. The system asks CALL‑E to place a patient intake call.
3. The patient provides:
   - reason for visit
   - symptoms
   - symptom duration
   - medications
   - relevant previous conditions
   - preferred appointment windows
   - language or accessibility needs
4. The backend stores a structured intake result.
5. If an emergency red flag appears, the system stops scheduling, gives emergency-care guidance, and flags the case for staff.
6. For a non-emergency case, the system finds available appointment slots.
7. The patient verbally confirms the exact date and time.
8. The system creates the appointment.
9. Staff review the clinic-ready intake summary.

## 6. Safety Rules

- Never diagnose.
- Never continue scheduling after a possible emergency red flag.
- Never create an appointment without explicit confirmation of the exact slot.
- Never use real patient health information in the demo.
- Use fake patients or consenting teammates only.
- Keep a staff-review state for every completed intake case.

## 7. Backend Components to Build

### Intake Case Management

Create and track one patient coordination case from staff start to final outcome.

Key statuses:

```text
STAFF_STARTED
CALL_PLANNED
CALL_IN_PROGRESS
INTAKE_RECEIVED
EMERGENCY_FLAGGED
SLOT_PROPOSED
WAITING_FOR_PATIENT_CONFIRMATION
APPOINTMENT_SCHEDULED
PATIENT_DECLINED
CALL_FAILED
STAFF_REVIEW_REQUIRED
```

### CALL‑E Adapter

Create a dedicated integration boundary that:

- sends the phone-call goal to CALL‑E
- stores the CALL‑E run identifier
- checks for terminal call results
- converts the result into our intake schema
- never books directly from raw transcript text

### Appointment Gateway

Create an interface that separates our workflow from the appointment system.

```text
findAvailableSlots(intakeCase)
bookConfirmedAppointment(intakeCase, slot)
```

First implementation:

```text
MockAppointmentGateway
```

Optional later implementation:

```text
OpenMrsRestClient
```

### Safety Classifier

Classify the intake as:

```text
ROUTINE
STAFF_REVIEW_REQUIRED
EMERGENCY_FLAGGED
```

Emergency classification ends the scheduling path immediately.

## 8. Data We Need

### Intake Case

- case ID
- patient reference
- phone number for demo use
- case status
- created time
- CALL‑E run ID
- selected appointment slot
- confirmation state
- staff review state

### Intake Result

- reason for visit
- symptoms
- duration
- medications
- relevant previous conditions
- preferred time windows
- language/accessibility needs
- urgency classification
- missing information
- CALL‑E summary or evidence reference

### Appointment

- appointment ID
- patient reference
- service type
- provider
- location
- date and time
- appointment status
- confirmation evidence

## 9. Dashboard Screens to Build

The frontend teammate builds:

1. **Patient cases list**  
   Shows active and completed intake cases.

2. **Start intake case form**  
   Lets staff select a fake patient and start the CALL‑E workflow.

3. **Case detail timeline**  
   Shows the current case status from call start through scheduling.

4. **Intake summary panel**  
   Shows symptoms, duration, medication, preferences, urgency, and missing information.

5. **Appointment confirmation panel**  
   Shows available slots, chosen slot, and verbal confirmation status.

6. **Staff review panel**  
   Shows the final pre-visit handoff summary.

## 10. Demo Requirement

The three-minute demo must prove this complete story:

```text
Staff starts case
→ CALL‑E performs a real patient call
→ structured intake appears
→ system proposes slot
→ patient confirms exact slot
→ appointment is scheduled
→ staff sees review-ready summary
```

A safe emergency case may be shown as a short second scenario or recorded fallback.

## 11. Team Responsibilities

### Backend Owner

- Java 25 and Spring Boot 4 backend
- API contract
- case lifecycle
- CALL‑E adapter
- mock appointment gateway
- safety classification
- automated tests

### Frontend Owner

- clinic dashboard
- case timeline
- intake summary
- appointment and confirmation UI
- staff review experience

### Process and Quality Owner

- fake patient data
- consent and emergency wording
- flow testing
- demo script and recording
- README and Devpost submission
- pull request preparation for `awesome-phone-call-agents`

## 12. Delivery Order

### Phase 1 — Environment Setup

- install JDK 25
- install Maven
- create Spring Boot 4 backend project
- verify the empty backend runs

### Phase 2 — Workflow Contract

- define case statuses
- define intake-result JSON
- define appointment-slot JSON
- agree on backend endpoints with frontend teammate

### Phase 3 — Working Vertical Slice

- create intake case
- run or simulate the CALL‑E result boundary
- store structured intake
- show intake in frontend
- find mock appointment slots
- book after confirmation

### Phase 4 — Real CALL‑E and Polish

- connect real CALL‑E call flow
- prove runtime CALL‑E use
- test failure and emergency paths
- prepare README, public demo, and submission pull request

## 13. Definition of Done

The project is ready to submit when:

- a real CALL‑E call occurs during the demonstrated workflow
- the backend stores structured intake data
- emergency cases cannot be scheduled
- appointments require explicit verbal confirmation
- the dashboard shows a staff-ready summary
- the project has a clear README and demo video
- a pull request is opened against `awesome-phone-call-agents`
