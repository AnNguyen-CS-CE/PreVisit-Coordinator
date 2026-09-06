# PreVisit Coordination

This context tracks the non-diagnostic information clinic staff use to prepare a pre-visit case for scheduling.

## Language

**Intake Case**:
A temporary coordination record for one fake patient's pre-visit process.
_Avoid_: Patient record, appointment

**Intake Submission**:
A one-time set of non-diagnostic answers supplied by staff for an existing intake case.
_Avoid_: Triage, diagnosis

**Intake Complete**:
The status of an intake case whose non-emergency intake submission has been recorded.
_Avoid_: Scheduled, confirmed

**Scheduling Halted**:
The status of an intake case with an emergency flag, which prevents scheduling in this application.
_Avoid_: Emergency diagnosis, triage result

**Scheduling Approval**:
The status of an intake-complete case that coordination staff may progress toward arranging an appointment.
_Avoid_: Appointment booked, appointment confirmed

**Appointment Slot**:
One exact mock time offered for an approved intake case, identified by its start and end time, provider, location, and service type.
_Avoid_: Appointment, confirmed appointment

**Proposed Slot**:
The one available appointment slot selected for discussion with the patient. It records intent only and does not create an appointment.
_Avoid_: Appointment booked, appointment confirmed

**Appointment Confirmation**:
An explicit `true` confirmation of the exact previously proposed appointment slot. It is required before the application creates an appointment.
_Avoid_: Implied consent, slot selection

**Appointment**:
A scheduled coordination record created only after an appointment confirmation for the exact proposed slot.
_Avoid_: Proposed slot, scheduling approval

**Coordination Staff**:
A staff member responsible for tracking intake cases and arranging appointment times through calls with patients.
_Avoid_: Clinical responder

**Staff Alert**:
A durable coordination record that informs coordination staff that an intake case is scheduling halted and requires their awareness. It remains active until staff record an alert resolution.
_Avoid_: Diagnosis, triage result, disposable notification

**Alert Resolution**:
A staff-recorded, non-diagnostic reason that moves a staff alert out of the active alert list. It does not change the intake case's scheduling-halted status or verify a medical outcome.
_Avoid_: Scheduling approval, medical clearance, diagnosis

**Alert Resolution Reason**:
The fixed non-diagnostic category staff select when resolving an alert: care follow-up recorded, referred to another facility, or other. A reason is accompanied by a factual staff note.
_Avoid_: Diagnosis, medical outcome, clinical recommendation

**Call Run**:
One recorded attempt to make a non-medical coordination call for an intake case. It identifies the provider-side run without retaining a call transcript in the intake case.
_Avoid_: Intake submission, appointment confirmation, call transcript

**Call Result**:
A validated, fixed-form non-diagnostic outcome of a call run: completed, recipient unavailable, or callback requested. It never represents a diagnosis, clinical assessment, or free-form transcript.
_Avoid_: Triage result, clinical note, raw call text
